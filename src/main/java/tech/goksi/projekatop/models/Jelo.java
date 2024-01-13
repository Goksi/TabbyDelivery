package tech.goksi.projekatop.models;

import javafx.scene.image.Image;

import java.util.Objects;

public class Jelo extends Model {
    private final String naziv;
    private final Image image;
    private final int cena;

    public Jelo(int id, String naziv, Image image, int cena) {
        super(id);
        this.naziv = naziv;
        this.cena = cena;
        this.image = image;
    }

    public String getNaziv() {
        return naziv;
    }

    public Image getImage() {
        return image;
    }

    public int getCena() {
        return cena;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jelo jelo = (Jelo) o;
        return getId() == jelo.getId() && cena == jelo.cena && naziv.equals(jelo.naziv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), naziv, cena);
    }
}
