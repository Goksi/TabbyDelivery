package tech.goksi.projekatop.persistance.impl;

import javafx.scene.image.Image;
import tech.goksi.projekatop.exceptions.KorisnikExistException;
import tech.goksi.projekatop.exceptions.RestoranExistException;
import tech.goksi.projekatop.models.Jelo;
import tech.goksi.projekatop.models.Korisnik;
import tech.goksi.projekatop.models.NarucenoJelo;
import tech.goksi.projekatop.models.Restoran;
import tech.goksi.projekatop.persistance.ConnectionWrapper;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.utils.EncryptionUtils;
import tech.goksi.projekatop.utils.PorudzbinaMaker;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/*TODO: update query different method*/
public class SQLiteImpl implements DataStorage {
    private static final Logger LOGGER = Logger.getLogger(SQLiteImpl.class.getName());
    private final ConnectionWrapper connection;

    public SQLiteImpl() {
        try {
            connection = ConnectionWrapper.getConnection("SQLite", "database.db");
            Statement statement = connection.createStatement();
            statement.addBatch("PRAGMA foreign_keys = ON");
            statement.addBatch("""
                    CREATE TABLE IF NOT EXISTS Korisnici(
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\s
                    username VARCHAR(48) NOT NULL UNIQUE,\s
                    password VARCHAR(128) NOT NULL,\s
                    creationDate DATETIME NOT NULL,\s
                    "admin" NOT NULL CHECK ("admin" IN (0, 1))
                    )
                    """);
            statement.addBatch("""
                    CREATE TABLE IF NOT EXISTS Restorani(
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\s
                    naziv VARCHAR(48) NOT NULL UNIQUE, \s
                    adresa VARCHAR(48) NOT NULL, \s
                    logo BLOB
                    )
                    """);
            statement.addBatch("""
                    CREATE TABLE IF NOT EXISTS Jela(
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \s
                    naziv VARCHAR(48) NOT NULL, \s
                    cena INTEGER NUT NULL CHECK (cena > 0), \s
                    image BLOB, \s
                    restoran INTEGER NOT NULL, \s
                    FOREIGN KEY(restoran) REFERENCES Restorani(id) ON DELETE CASCADE
                    )
                    """);
            statement.addBatch("""
                    CREATE TABLE IF NOT EXISTS Porudzbine(
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \s
                    korisnik INTEGER NOT NULL, \s
                    vreme DATETIME NOT NULL, \s
                    FOREIGN KEY(korisnik) REFERENCES Korisnici(id) ON DELETE CASCADE
                    )
                    """);
            statement.addBatch("""
                    CREATE TABLE IF NOT EXISTS Porudzbine_Jela(
                    jelo INTEGER NOT NULL, \s
                    porudzbina INTEGER NOT NULL, \s
                    kolicina INTEGER NOT NULL CHECK (kolicina >= 1), \s
                    PRIMARY KEY(jelo, porudzbina), \s
                    FOREIGN KEY(jelo) REFERENCES Jela(id) ON DELETE CASCADE, \s
                    FOREIGN KEY(porudzbina) REFERENCES Porudzbine(id) ON DELETE CASCADE
                    )
                    """);
            statement.executeBatch();
            statement.close();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Greska u povezivanju sa databazom !", e);
            System.exit(1);
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<List<Korisnik>> getAllUsers() {
        return CompletableFuture.supplyAsync(() -> connection.withConnection("SELECT * FROM Korisnici", preparedStatement -> {
            List<Korisnik> korisnici = new ArrayList<>();
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    Date creationDate = resultSet.getDate("creationDate");
                    boolean admin = resultSet.getBoolean("admin");
                    korisnici.add(new Korisnik(id, username, password, creationDate, admin));
                }
                return korisnici;
            } catch (SQLException e) {
                return null;
            }
        }));
    }

    @Override
    public CompletableFuture<Korisnik> findUserByUsername(String username) {
        return CompletableFuture.supplyAsync(() -> connection.withConnection("SELECT * FROM Korisnici WHERE username = ?", preparedStatement -> {
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String password = resultSet.getString("password");
                    Date creationDate = resultSet.getDate("creationDate");
                    boolean admin = resultSet.getBoolean("admin");
                    return new Korisnik(id, username, password, creationDate, admin);
                }
                return null;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Greska u pronalazenju korisnika u databazi !", e);
                return null;
            }
        }, username));
    }

    @Override
    public CompletableFuture<Void> addUser(String username, String password, boolean admin) {
        return CompletableFuture.runAsync(() -> {
            Korisnik korisnik = findUserByUsername(username).join();
            if (korisnik != null) throw new KorisnikExistException(korisnik.getUsername());
            connection.withConnection("INSERT INTO Korisnici(username, password, creationDate, admin) VALUES (?, ?, ?, ?)", preparedStatement -> {
                try {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Greska u dodavanju korisnika u bazu podataka !", e);
                }
            }, username, EncryptionUtils.createHash(password), Date.from(Instant.now()), admin);
        });
    }

    @Override
    public CompletableFuture<Korisnik> changePassword(Korisnik korisnik, String password) {
        return CompletableFuture.supplyAsync(() -> {
            connection.withConnection("UPDATE Korisnici SET password = ? WHERE id = ?", preparedStatement -> {
                try {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Greska pri promeni lozinke !", e);
                }
            }, EncryptionUtils.createHash(password), korisnik.getId());

            return findUserByUsername(korisnik.getUsername());
        }).thenCompose(Function.identity());
    }

    @Override
    public CompletableFuture<Void> removeUser(Korisnik korisnik) {
        return CompletableFuture.runAsync(() -> {
            connection.withConnection("DELETE FROM Korisnici WHERE id = ?", preparedStatement -> {
                try {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Greska prilikom brisanja korisnika !", e);
                }
            }, korisnik.getId());
        });
    }

    @Override
    public CompletableFuture<Void> modifyUser(Korisnik korisnik, Map<String, Object> fields) {
        if (fields.isEmpty()) throw new IllegalArgumentException("Makar jedna stvar mora biti promenjena !");
        Object[] params = Arrays.copyOf(fields.values().toArray(), fields.size() + 1);
        params[fields.size()] = korisnik.getId();
        String query = buildUpdateQuery("UPDATE Korisnici SET %s WHERE id = ?", fields);
        return CompletableFuture.runAsync(() -> {
            String username = (String) fields.get("username");
            if (username != null) {
                Korisnik drugi = findUserByUsername(username).join();
                if (drugi != null) throw new KorisnikExistException(drugi.getUsername());
            }
            connection.withConnection(query, preparedStatement -> {
                try {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Greska prilikom uredjivanja korisnika !", e);
                }
            }, params);
        });
    }

    @Override
    public CompletableFuture<List<Restoran>> getAllRestorans() {
        return CompletableFuture.supplyAsync(() -> {
            List<Restoran> restorani = new ArrayList<>();
            connection.withConnection("SELECT * FROM Restorani", restoranStatement -> {
                try {
                    ResultSet restoranResultSet = restoranStatement.executeQuery();
                    while (restoranResultSet.next()) {
                        int id = restoranResultSet.getInt("id");
                        String naziv = restoranResultSet.getString("naziv");
                        String adresa = restoranResultSet.getString("adresa");
                        byte[] logoBytes = restoranResultSet.getBytes("logo");
                        Image logo = logoBytes != null ? new Image(new ByteArrayInputStream(logoBytes)) : null;
                        Set<Jelo> jela = new HashSet<>();
                        connection.withConnection("SELECT * FROM Jela WHERE restoran = ?", jeloStatement -> {
                            try {
                                ResultSet jeloResultSet = jeloStatement.executeQuery();
                                while (jeloResultSet.next()) {
                                    int idJela = jeloResultSet.getInt("id");
                                    String jeloNaziv = jeloResultSet.getString("naziv");
                                    int jeloCena = jeloResultSet.getInt("cena");
                                    byte[] jeloBytes = jeloResultSet.getBytes("image");
                                    Image slika = jeloBytes != null ? new Image(new ByteArrayInputStream(jeloBytes)) : null;
                                    jela.add(new Jelo(idJela, jeloNaziv, slika, jeloCena));
                                }
                            } catch (SQLException e) {
                                LOGGER.log(Level.SEVERE, "Greska pri preuzimanju jela iz baze !", e);
                            }
                        }, id);

                        restorani.add(new Restoran(id, naziv, adresa, logo, jela));
                    }
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Greska pri preuzimanju restorana iz baze !", e);
                }
            });
            return restorani;
        });
    }

    @Override
    public CompletableFuture<Void> addRestoran(String naziv, String adresa, InputStream slika) {
        return CompletableFuture.runAsync(() -> {
            connection.withConnection("INSERT INTO Restorani(naziv, adresa, logo) VALUES (?, ?, ?)", preparedStatement -> {
                try {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                        throw new RestoranExistException(naziv);
                    } else {
                        LOGGER.log(Level.SEVERE, "Greska pri ubacivanju restorana u bazu !", e);
                    }
                }
            }, naziv, adresa, slika);
            try {
                if (slika != null) {
                    slika.close();
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Greska pri zatvaranju strima !", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> removeRestoran(Restoran restoran) {
        return CompletableFuture.runAsync(() -> {
            connection.withConnection("DELETE FROM Restorani WHERE id = ?", preparedStatement -> {
                try {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Greska pri brisanju restorana iz baze !", e);
                }
            }, restoran.getId());
        });
    }

    @Override
    public CompletableFuture<Void> modifyRestoran(Restoran restoran, Map<String, Object> fields) {
        if (fields.isEmpty()) throw new IllegalArgumentException("Makar jedna stvar mora biti promenjena !");
        Object[] params = Arrays.copyOf(fields.values().toArray(), fields.size() + 1);
        params[fields.size()] = restoran.getId();
        String query = buildUpdateQuery("UPDATE Restorani SET %s WHERE id = ?", fields);
        return CompletableFuture.runAsync(() -> {
            connection.withConnection(query, preparedStatement -> {
                try {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                        throw new RestoranExistException((String) fields.get("naziv"));
                    } else LOGGER.log(Level.SEVERE, "Greska pri modifikovanju restorana !", e);
                }
            }, params);
        });
    }

    @Override
    public CompletableFuture<Jelo> addJeloToRestoran(Restoran restoran, String naziv, InputStream imageStream, int cena) {
        return CompletableFuture.supplyAsync(() -> {
            connection.withConnection("INSERT INTO Jela(naziv, cena, image, restoran) VALUES (?, ?, ?, ?)", preparedStatement -> {
                try {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Greska pri dodavanju jela u restoran !", e);
                }
            }, naziv, cena, imageStream, restoran.getId());
            return connection.withConnection("SELECT * FROM Jela WHERE naziv = ?", preparedStatement -> {
                try {
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (!resultSet.next()) return null;
                    int id = resultSet.getInt("id");
                    byte[] slikaBytes = resultSet.getBytes("image");
                    Image slika = slikaBytes != null ? new Image(new ByteArrayInputStream(slikaBytes)) : null;
                    return new Jelo(id, naziv, slika, cena);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Greska pri citanju dodatog jela !", e);
                }
                return null;
            }, naziv);
        });
    }

    @Override
    public CompletableFuture<Void> obrisiJelo(Jelo jelo) {
        return CompletableFuture.runAsync(() -> {
            connection.withConnection("DELETE FROM Jela WHERE id = ?", preparedStatement -> {
                try {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Greska pri brisanju jela iz restorana !", e);
                }
            }, jelo.getId());
        });
    }

    @Override
    public CompletableFuture<Void> dodajPorudzbinu(Korisnik korisnik, PorudzbinaMaker porudzbinaMaker) {
        return CompletableFuture.runAsync(() -> {
            connection.withConnection("INSERT INTO Porudzbine(korisnik, vreme) VALUES (?, ?)", preparedStatement -> {
                try {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Greska pri dodavanju porudzbine !", e);
                    throw new IllegalStateException("Porudzbina nije dodata !");
                }
            }, korisnik.getId(), Date.from(Instant.now()));
            int porudzbinaId = connection.withConnection("SELECT last_insert_rowid()", preparedStatement -> {
                try {
                    ResultSet resultSet = preparedStatement.executeQuery();
                    return resultSet.getInt(1);
                } catch (SQLException e) {
                    return -1;
                }
            });
            if (porudzbinaId == -1) return;
            List<NarucenoJelo> narucenaJela = porudzbinaMaker.getJela();
            Object[] args = new Object[narucenaJela.size() * 3];
            int counter = 0;
            String baseQuery = "INSERT INTO Porudzbine_Jela(jelo, porudzbina, kolicina) VALUES %s";
            StringJoiner joiner = new StringJoiner(",");
            for (NarucenoJelo narucenoJelo : narucenaJela) {
                joiner.add("(?, ?, ?)");
                args[counter++] = narucenoJelo.getJelo().getId();
                args[counter++] = porudzbinaId;
                args[counter++] = narucenoJelo.getCount();
            }
            String query = baseQuery.formatted(joiner.toString());
            connection.withConnection(query, preparedStatement -> {
                try {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Greska pri povezivanju jela sa porudzbinom !", e);
                }
            }, args);
        });
    }

    private String buildUpdateQuery(String template, Map<String, Object> fields) {
        StringJoiner updateBuilder = new StringJoiner(",");
        for (String key : fields.keySet()) {
            updateBuilder.add(key + "=?");
        }
        return String.format(template, updateBuilder);
    }

}
