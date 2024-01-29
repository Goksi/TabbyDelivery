package tech.goksi.projekatop.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;

import java.util.Optional;

public class PorudzbinaMaker {
    private final ListProperty<NarucenoJelo> narucenaJela;
    private final IntegerProperty ukupnaCenaProperty;

    public PorudzbinaMaker() {
        narucenaJela = new SimpleListProperty<>();
        ukupnaCenaProperty = new SimpleIntegerProperty();
    }

    public void dodajJelo(Jelo jelo) {
        NarucenoJelo narucenoJelo = findJelo(jelo)
                .orElseGet(() -> {
                    NarucenoJelo novoNarucenoJelo = new NarucenoJelo(jelo);
                    narucenaJela.add(novoNarucenoJelo);
                    return novoNarucenoJelo;
                });
        narucenoJelo.addQuantity();
        ukupnaCenaProperty.add(narucenoJelo.getJelo().getCena());
    }

    public void obrisiJelo(Jelo jelo) {
        NarucenoJelo narucenoJelo = findJelo(jelo).orElse(null);
        if (narucenoJelo == null) return;
        narucenoJelo.removeQuantity();
        if (narucenoJelo.getCount() == 0) {
            narucenaJela.remove(narucenoJelo);
        }
        ukupnaCenaProperty.subtract(narucenoJelo.getJelo().getCena());
    }

    public ListProperty<NarucenoJelo> narucenaJelaProperty() {
        return narucenaJela;
    }

    public IntegerProperty ukupnaCenaPropertyProperty() {
        return ukupnaCenaProperty;
    }

    private Optional<NarucenoJelo> findJelo(Jelo jelo) {
        return narucenaJela.stream()
                .filter(naruceno -> naruceno.getJelo().equals(jelo))
                .findFirst();
    }

}
