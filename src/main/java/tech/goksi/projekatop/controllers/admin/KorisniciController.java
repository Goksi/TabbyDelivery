package tech.goksi.projekatop.controllers.admin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
        ContextMenu contextMenu = new ContextMenu();
        MenuItem urediKorisnika = new MenuItem("Uredi korisnika");
        MenuItem obrisiKorisnika = new MenuItem("Obrisi korisnika");
        urediKorisnika.addEventHandler(ActionEvent.ACTION, this::onUrediKorisnika);
        obrisiKorisnika.addEventHandler(ActionEvent.ACTION, this::onObrisiKorisnika);

        contextMenu.getItems().add(urediKorisnika);
        contextMenu.getItems().add(obrisiKorisnika);
        korisniciListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                korisniciListView.setContextMenu(null);
            } else {
                korisniciListView.setContextMenu(contextMenu);
            }
        });
        korisniciListView.setItems(korisniciList);
        populateListView();
    }

    public void onUrediKorisnika(ActionEvent event) {

    }

    public void onObrisiKorisnika(ActionEvent event) {
        Korisnik korisnik = korisniciListView.getSelectionModel().getSelectedItem();
        Alert confirmation = new Alert(
                Alert.AlertType.WARNING,
                "Da li ste sigurni da zelite da obrisete korisnika " + korisnik.getUsername() + "?",
                ButtonType.YES,
                ButtonType.NO
        );
        confirmation.setHeaderText(null);
        confirmation.setTitle("Upozorenje");
        confirmation.showAndWait();
        if (confirmation.getResult() == ButtonType.YES) {
            storage.removeUser(korisnik)
                    .thenRun(this::populateListView);
        }
    }

    private void populateListView() {
        if (Platform.isFxApplicationThread()) {
            loadingSpinner.setVisible(true);
        } else {
            Platform.runLater(() -> loadingSpinner.setVisible(true));
        }
        storage.getAllUsers()
                .thenAccept(list -> Platform.runLater(() -> korisniciList.setAll(list)))
                .thenRun(() -> Platform.runLater(() -> loadingSpinner.setVisible(false)));
    }


    @Override
    public void setDataStorage(DataStorage storage) {

        this.storage = storage;
    }
}
