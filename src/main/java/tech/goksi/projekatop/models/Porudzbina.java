package tech.goksi.projekatop.models;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

public class Porudzbina extends Model {
    private final Korisnik korisnik;
    private final Date vremePorudzbine;
    private final Set<NarucenoJelo> narucenoJela;

    public Porudzbina(int id, Korisnik korisnik, Date vremePorudzbine, Set<NarucenoJelo> narucenoJela) {
        super(id);
        this.korisnik = korisnik;
        this.vremePorudzbine = vremePorudzbine;
        this.narucenoJela = narucenoJela;
    }

    public Korisnik getKorisnik() {
        return korisnik;
    }

    public Date getVremePorudzbine() {
        return vremePorudzbine;
    }

    public Set<NarucenoJelo> getNarucenoJela() {
        return Collections.unmodifiableSet(narucenoJela);
    }
}
