package tech.goksi.projekatop.controllers.admin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import tech.goksi.projekatop.models.Korisnik;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.persistance.DataStorageInjectable;

public class KorisniciController implements DataStorageInjectable {


    @FXML
    private ListView<Korisnik> korisniciListView;
    @FXML
    private ProgressIndicator loadingSpinner;
    private ObservableList<Korisnik> korisniciList;
    private DataStorage storage;

    public KorisniciController() {
        korisniciList = FXCollections.observableArrayList();
    }

    public void initialize() {
        korisniciListView.setCellFactory(korisniciView -> new ListCell<>() {
            @Override
            protected void updateItem(Korisnik korisnik, boolean empty) {
                super.updateItem(korisnik, empty);
                if (empty || korisnik == null) {
                    setText(null);
                } else {
                    setText(String.format(
                            "Username: %s   |   Admin: %s   |   Datum reg: %s",
                            korisnik.getUsername(),
                            korisnik.isAdmin() ? "DA" : "NE",
                            korisnik.getDatumRegistracije())
                    );
                }
            }
        });

        korisniciListView.setItems(korisniciList);
        storage.getAllUsers()
                .thenAccept(korisniciList::addAll)
                .thenRun(() -> Platform.runLater(() -> loadingSpinner.setVisible(false)));
        System.out.println(korisniciList);
    }

    @Override
    public void setDataStorage(DataStorage storage) {

        this.storage = storage;
    }
}
