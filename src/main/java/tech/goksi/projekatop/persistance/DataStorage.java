package tech.goksi.projekatop.persistance;

import tech.goksi.projekatop.models.Korisnik;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DataStorage {
    CompletableFuture<List<Korisnik>> getAllUsers();

    CompletableFuture<Korisnik> findUserByUsername(String username);


    CompletableFuture<Void> addUser(String username, String password, boolean admin);

    CompletableFuture<Korisnik> changePassword(Korisnik korisnik, String password);
}
