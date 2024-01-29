package tech.goksi.projekatop.models;

public class NarucenoJelo {
    private final Jelo jelo;
    private int count;

    public NarucenoJelo(Jelo jelo) {
        this.jelo = jelo;
        this.count = 0;
    }

    public void addQuantity() {
        count++;
    }

    public void removeQuantity() {
        count--;
    }

    public Jelo getJelo() {
        return jelo;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "Naziv: %s Kolicina: x%d".formatted(jelo.getNaziv(), count);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NarucenoJelo that = (NarucenoJelo) o;

        if (count != that.count) return false;
        return jelo.equals(that.jelo);
    }

    @Override
    public int hashCode() {
        int result = jelo.hashCode();
        result = 31 * result + count;
        return result;
    }
}
