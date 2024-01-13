package tech.goksi.projekatop.models;

import javafx.scene.image.Image;

import java.util.Collections;
import java.util.List;

public class Restoran extends Model {
    private final String naziv;
    private final String adresa;
    private final Image logo;
    private final List<Jelo> jela;

    protected Restoran(int id, String naziv, String adresa, Image logo, List<Jelo> jela) {
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

    public List<Jelo> getJela() {
        return Collections.unmodifiableList(jela);
    }
}
