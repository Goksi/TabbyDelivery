package tech.goksi.projekatop.controllers.porudzbine;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import tech.goksi.projekatop.models.NarucenoJelo;
import tech.goksi.projekatop.models.PorudzbinaMaker;
import tech.goksi.projekatop.models.Restoran;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.persistance.DataStorageInjectable;

public class PorudzbinaController implements DataStorageInjectable {
    private final PorudzbinaMaker trenutnaPorudzbina;
    private final SimpleObjectProperty<Restoran> restoranProperty;
    @FXML
    private ListView<NarucenoJelo> racunListView;
    @FXML
    private GridPane gridPane;
    private Runnable backer;
    private DataStorage storage;

    public PorudzbinaController() {
        trenutnaPorudzbina = new PorudzbinaMaker();
        restoranProperty = new SimpleObjectProperty<>();
    }

    @SuppressWarnings("unchecked")
    public void initialize() {
        racunListView.itemsProperty().bind(trenutnaPorudzbina.narucenaJelaProperty());

        racunListView.sceneProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                Pair<Runnable, Restoran> userData = (Pair<Runnable, Restoran>) newValue.getRoot().getUserData();
                restoranProperty.set(userData.getValue());
                backer = userData.getKey();

            }
        });
    }

    @Override
    public void setDataStorage(DataStorage storage) {
        this.storage = storage;
    }

    public void onBack(ActionEvent event) {
        backer.run();
    }
}
