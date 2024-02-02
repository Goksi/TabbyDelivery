package tech.goksi.projekatop.persistance;

import tech.goksi.projekatop.models.Jelo;
import tech.goksi.projekatop.models.Korisnik;
import tech.goksi.projekatop.models.Porudzbina;
import tech.goksi.projekatop.models.Restoran;
import tech.goksi.projekatop.utils.PorudzbinaMaker;

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

    CompletableFuture<Jelo> addJeloToRestoran(Restoran restoran, String naziv, InputStream slika, int cena);

    CompletableFuture<Void> obrisiJelo(Jelo jelo);

    CompletableFuture<Void> dodajPorudzbinu(Korisnik korisnik, PorudzbinaMaker porudzbinaMaker);

    CompletableFuture<List<Porudzbina>> getAllPorudzbine();

    default CompletableFuture<List<Porudzbina>> getAllPorudzbine(Korisnik korisnik) {
        return getAllPorudzbine()
                .thenApply(porudzbine -> porudzbine
                        .stream()
                        .filter(porudzbina -> korisnik.equals(porudzbina.getKorisnik()))
                        .toList()
                );
    }

    CompletableFuture<Void> obrisiPorudzbinu(Porudzbina porudzbina);
}
