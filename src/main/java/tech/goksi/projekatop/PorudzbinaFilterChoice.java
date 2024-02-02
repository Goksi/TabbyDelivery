package tech.goksi.projekatop;

public enum PorudzbinaFilterChoice {
    ID("ID-u"),
    KORISNIK("Korisnik-u");

    private final String representation;

    PorudzbinaFilterChoice(String representation) {
        this.representation = representation;
    }

    @Override
    public String toString() {
        return representation;
    }
}
