package tech.goksi.projekatop.exceptions;

public class KorisnikExistException extends RuntimeException {
    public KorisnikExistException(String username) {
        super(String.format("Korisnik sa username-om %s vec postoji u sistemu !", username));
    }
}
