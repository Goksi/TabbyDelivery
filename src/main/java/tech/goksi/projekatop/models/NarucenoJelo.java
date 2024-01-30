package tech.goksi.projekatop.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class NarucenoJelo {
    private final Jelo jelo;
    private final IntegerProperty countProperty;

    public NarucenoJelo(Jelo jelo) {
        this.jelo = jelo;
        this.countProperty = new SimpleIntegerProperty(0);
    }

    public void addQuantity() {
        countProperty.set(countProperty.get() + 1);
    }

    public void removeQuantity() {
        countProperty.set(countProperty.get() - 1);
    }

    public Jelo getJelo() {
        return jelo;
    }

    public IntegerProperty countProperty() {
        return countProperty;
    }

    public int getCena() {
        return jelo.getCena() * countProperty.get();
    }

    public int getCount() {
        return countProperty.get();
    }

    @Override
    public String toString() {
        return "Naziv: %s Kolicina: x%d".formatted(jelo.getNaziv(), countProperty.get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NarucenoJelo that = (NarucenoJelo) o;

        if (countProperty != that.countProperty) return false;
        return jelo.equals(that.jelo);
    }

    @Override
    public int hashCode() {
        int result = jelo.hashCode();
        result = 31 * result + countProperty.get();
        return result;
    }
}
