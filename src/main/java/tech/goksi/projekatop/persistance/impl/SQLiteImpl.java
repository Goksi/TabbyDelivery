package tech.goksi.projekatop.persistance.impl;

import tech.goksi.projekatop.exceptions.KorisnikExistException;
import tech.goksi.projekatop.models.Korisnik;
import tech.goksi.projekatop.persistance.ConnectionWrapper;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.utils.EncryptionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteImpl implements DataStorage {
    private static final Logger LOGGER = Logger.getLogger(SQLiteImpl.class.getName());
    private final ConnectionWrapper connection;

    public SQLiteImpl() {
        try {
            connection = ConnectionWrapper.getConnection("SQLite", "database.db");
            Statement statement = connection.createStatement();
            statement.addBatch("""
                    CREATE TABLE IF NOT EXISTS Korisnici(
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\s
                    username VARCHAR(48) NOT NULL UNIQUE,\s
                    password VARCHAR(128) NOT NULL,\s
                    "admin" NOT NULL CHECK ("admin" IN (0, 1))
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
                    boolean admin = resultSet.getBoolean("admin");
                    korisnici.add(new Korisnik(id, username, password, admin));
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
                    boolean admin = resultSet.getBoolean("admin");
                    return new Korisnik(id, username, password, admin);
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
            connection.withConnection("INSERT INTO Korisnici(username, password, admin) VALUES (?, ?, ?)", preparedStatement -> {
                try {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Greska u dodavanju korisnika u bazu podataka !", e);
                }
            }, username, EncryptionUtils.createHash(password), admin);
        });
    }
}