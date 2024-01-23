package tech.goksi.projekatop.persistance;

import tech.goksi.projekatop.models.Jelo;
import tech.goksi.projekatop.models.Korisnik;
import tech.goksi.projekatop.models.Restoran;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface DataStorage {
    CompletableFuture<List<Korisnik>> getAllUsers();

    CompletableFuture<Korisnik> findUserByUsername(String username);

    CompletableFuture<Void> addUser(String username, String password, boolean admin);

    CompletableFuture<Korisnik> changePassword(Korisnik korisnik, String password);

    CompletableFuture<Void> removeUser(Korisnik korisnik);

    CompletableFuture<Void> modifyUser(Korisnik korisnik, Map<String, Object> fields);

    CompletableFuture<List<Restoran>> getAllRestorans();

    CompletableFuture<Void> addRestoran(String naziv, String adresa, InputStream slika);

    CompletableFuture<Void> removeRestoran(Restoran restoran);

    CompletableFuture<Void> modifyRestoran(Restoran restoran, Map<String, Object> fields);

    CompletableFuture<Void> addJeloToRestoran(Restoran restoran, String naziv, InputStream slika, int cena);

    CompletableFuture<Void> obrisiJelo(Jelo jelo);
}
