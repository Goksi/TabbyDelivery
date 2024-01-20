package tech.goksi.projekatop.exceptions;

public class RestoranExistException extends RuntimeException {
    public RestoranExistException(String restoran) {
        super(String.format("Restoran pod nazivom %s vec postoji !", restoran));
    }
}
