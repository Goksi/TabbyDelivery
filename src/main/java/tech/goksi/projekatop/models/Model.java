package tech.goksi.projekatop.models;

public abstract class Model {
    private final int id;

    protected Model(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
