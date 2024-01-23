package tech.goksi.projekatop.controllers.admin;

import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.persistance.DataStorageInjectable;

public class DodajJeloController implements DataStorageInjectable {
    private DataStorage storage;

    @Override
    public void setDataStorage(DataStorage storage) {
        this.storage = storage;
    }
}
