package tech.goksi.projekatop.models;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;

public class PorudzbinaMaker {
    private final ListProperty<NarucenoJelo> narucenaJela;

    public PorudzbinaMaker() {
        narucenaJela = new SimpleListProperty<>();
    }

    public void dodajJelo(Jelo jelo) {
        NarucenoJelo narucenoJelo = findJelo(jelo);
        narucenoJelo.addQuantity();
    }

    public void obrisiJelo(Jelo jelo) {
        NarucenoJelo narucenoJelo = findJelo(jelo);
        narucenoJelo.removeQuantity();
        if (narucenoJelo.getCount() == 0) {
            narucenaJela.remove(narucenoJelo);
        }
    }

    public ListProperty<NarucenoJelo> narucenaJelaProperty() {
        return narucenaJela;
    }

    private NarucenoJelo findJelo(Jelo jelo) {
        return narucenaJela.stream()
                .filter(naruceno -> naruceno.getJelo().equals(jelo))
                .findFirst()
                .orElseGet(() -> {
                    NarucenoJelo novoNarucenoJelo = new NarucenoJelo(jelo);
                    narucenaJela.add(novoNarucenoJelo);
                    return novoNarucenoJelo;
                });
    }

}
