package tech.goksi.projekatop.models;

import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.List;
import java.util.Optional;

public class PorudzbinaMaker {
    private final ListProperty<NarucenoJelo> narucenaJela;
    private final IntegerProperty ukupnaCenaProperty;

    public PorudzbinaMaker() {
        narucenaJela = new SimpleListProperty<>(FXCollections.observableArrayList(narucenoJelo -> new Observable[]{narucenoJelo.countProperty()}));
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
        ukupnaCenaProperty.set(ukupnaCenaProperty.get() + narucenoJelo.getJelo().getCena());
    }

    public void obrisiJelo(Jelo jelo) {
        NarucenoJelo narucenoJelo = findJelo(jelo).orElse(null);
        if (narucenoJelo == null) return;
        narucenoJelo.removeQuantity();
        if (narucenoJelo.getCount() == 0) {
            narucenaJela.remove(narucenoJelo);
        }
        ukupnaCenaProperty.set(ukupnaCenaProperty.get() - narucenoJelo.getJelo().getCena());
    }

    public void reset() {
        narucenaJela.clear();
        ukupnaCenaProperty.set(0);
    }

    public void obrisiSvaJela(Jelo jelo) {
        NarucenoJelo narucenoJelo = findJelo(jelo).orElse(null);
        if (narucenoJelo == null) return;
        narucenaJela.remove(narucenoJelo);
        ukupnaCenaProperty.set(ukupnaCenaProperty.get() - narucenoJelo.getCena());
    }

    public boolean isEmpty() {
        return narucenaJela.isEmpty();
    }

    public ListProperty<NarucenoJelo> narucenaJelaProperty() {
        return narucenaJela;
    }

    public IntegerProperty ukupnaCenaPropertyProperty() {
        return ukupnaCenaProperty;
    }

    public List<NarucenoJelo> getJela() {
        return narucenaJelaProperty().get();
    }

    private Optional<NarucenoJelo> findJelo(Jelo jelo) {
        return narucenaJela.stream()
                .filter(naruceno -> naruceno.getJelo().equals(jelo))
                .findFirst();
    }

}
