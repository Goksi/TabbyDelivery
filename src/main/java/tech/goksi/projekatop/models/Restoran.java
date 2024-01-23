package tech.goksi.projekatop.models;

import javafx.scene.image.Image;

import java.util.Collections;
import java.util.Set;

public class Restoran extends Model {
    private final String naziv;
    private final String adresa;
    private final Image logo;
    private final Set<Jelo> jela;

    public Restoran(int id, String naziv, String adresa, Image logo, Set<Jelo> jela) {
        super(id);
        this.naziv = naziv;
        this.adresa = adresa;
        this.logo = logo;
        this.jela = jela;
    }

    public String getNaziv() {
        return naziv;
    }

    public String getAdresa() {
        return adresa;
    }

    public Image getLogo() {
        return logo;
    }

    public Set<Jelo> getJela() {
        return Collections.unmodifiableSet(jela);
    }
}
