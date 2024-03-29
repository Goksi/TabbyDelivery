package tech.goksi.projekatop.controllers.admin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import tech.goksi.projekatop.TabbyViews;
import tech.goksi.projekatop.models.Korisnik;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.utils.Injectable;
import tech.goksi.projekatop.utils.ViewLoader;

public class KorisniciController implements Injectable {
    private final DataStorage storage;
    @FXML
    private ListView<Korisnik> korisniciListView;
    @FXML
    private ProgressIndicator loadingSpinner;
    private ObservableList<Korisnik> korisniciList;

    public KorisniciController(DataStorage storage) {
        korisniciList = FXCollections.observableArrayList();
        this.storage = storage;
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
            korisniciListView.setContextMenu(newValue == null ? null : contextMenu);
        });
        korisniciListView.setItems(korisniciList);
        populateListView();
    }

    public void onUrediKorisnika(ActionEvent actionEvent) {
        MenuItem menuItem = (MenuItem) actionEvent.getSource();
        menuItem.setDisable(true);
        Korisnik korisnik = korisniciListView.getSelectionModel().getSelectedItem();
        Stage stage = makeStage(korisnik);
        stage.showAndWait();
        menuItem.setDisable(false);
    }

    public void onDodajKorisnika(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);
        Stage stage = makeStage(null);
        stage.showAndWait();
        button.setDisable(false);
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

    @SuppressWarnings("ConstantConditions")
    private Stage makeStage(Korisnik korisnik) {
        Stage stage = new Stage();
        if (korisnik == null) {
            stage.setTitle("TabbyDelivery | Dodaj korisnika");
        } else stage.setTitle("TabbyDelivery | Uredi korisnika");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        stage.setResizable(false);
        Parent parent = ViewLoader.load(TabbyViews.MODIFY_KORISNIK, storage, korisnik);
        stage.setScene(new Scene(parent));
        stage.setOnHiding(event -> populateListView());
        return stage;
    }

    private void populateListView() {
        if (Platform.isFxApplicationThread()) {
            loadingSpinner.setVisible(true);
        } else {
            Platform.runLater(() -> loadingSpinner.setVisible(true));
        }
        storage.getAllUsers()
                .thenAccept(list -> Platform.runLater(() -> {
                    korisniciList.setAll(list);
                    loadingSpinner.setVisible(false);
                }));
    }
}
