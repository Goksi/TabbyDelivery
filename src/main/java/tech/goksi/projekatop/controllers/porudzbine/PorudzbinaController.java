package tech.goksi.projekatop.controllers.porudzbine;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import tech.goksi.projekatop.TabbyViews;
import tech.goksi.projekatop.models.Jelo;
import tech.goksi.projekatop.models.Korisnik;
import tech.goksi.projekatop.models.NarucenoJelo;
import tech.goksi.projekatop.models.Restoran;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.utils.Injectable;
import tech.goksi.projekatop.utils.PorudzbinaMaker;
import tech.goksi.projekatop.utils.ViewLoader;

import java.util.Set;

public class PorudzbinaController implements Injectable {
    private final PorudzbinaMaker trenutnaPorudzbina;
    private final Restoran restoran;
    private final DataStorage storage;
    private final Korisnik trenutniKorisnik;
    @FXML
    private ListView<NarucenoJelo> racunListView;
    @FXML
    private Label cenaLabel;
    @FXML
    private GridPane gridPane;

    public PorudzbinaController(DataStorage storage, Restoran restoran, Korisnik trenutniKorisnik) {
        trenutnaPorudzbina = new PorudzbinaMaker();
        this.storage = storage;
        this.restoran = restoran;
        this.trenutniKorisnik = trenutniKorisnik;
    }

    public void initialize() {
        racunListView.itemsProperty().bind(trenutnaPorudzbina.narucenaJelaProperty());
        String cenaPattern = cenaLabel.getText();
        cenaLabel.setText(cenaPattern.formatted(0));
        cenaLabel.textProperty().bind(trenutnaPorudzbina.ukupnaCenaPropertyProperty().asString(cenaPattern));
        Set<Jelo> jela = restoran.getJela();
        int col = 0, row = 0;
        for (Jelo jelo : jela) {
            gridPane.add(ViewLoader.load(TabbyViews.JELO_KARTICA, jelo, trenutnaPorudzbina), col, row);
            col++;
            if (col == 2) {
                col = 0;
                row++;
            }
        }
        ContextMenu contextMenu = new ContextMenu();
        MenuItem obrisiJedan = new MenuItem("Obrisi jedan");
        MenuItem obrisiSve = new MenuItem("Obrisi sve");
        obrisiJedan.addEventHandler(ActionEvent.ACTION, this::onObrisiJedan);
        obrisiSve.addEventHandler(ActionEvent.ACTION, this::onObrisiSve);
        contextMenu.getItems().add(obrisiJedan);
        contextMenu.getItems().add(obrisiSve);

        racunListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            racunListView.setContextMenu(newValue == null ? null : contextMenu);
        });
    }

    public void onObrisiJedan(ActionEvent actionEvent) {
        Jelo jelo = racunListView.getSelectionModel().getSelectedItem().getJelo();
        trenutnaPorudzbina.obrisiJelo(jelo);
    }

    public void onObrisiSve(ActionEvent actionEvent) {
        Jelo jelo = racunListView.getSelectionModel().getSelectedItem().getJelo();
        trenutnaPorudzbina.obrisiSvaJela(jelo);
    }

    public void onResetujPorudzbinu() {
        trenutnaPorudzbina.reset();
    }

    public void onZavrsiPorudzbinu() {
        if (trenutnaPorudzbina.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Ne mozete predati praznu porudzbinu !", ButtonType.OK);
            alert.setHeaderText(null);
            alert.setTitle("Upozorenje");
            alert.show();
            return;
        }
        storage.dodajPorudzbinu(trenutniKorisnik, trenutnaPorudzbina)
                .whenComplete((unused, throwable) -> {
                    if (throwable != null) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Greska pri dodavanju porudzbine, proverite log !", ButtonType.OK);
                            alert.setHeaderText(null);
                            alert.setTitle("Greska");
                            alert.show();
                        });
                        return;
                    }
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Uspesno zavrsena porudzbina !", ButtonType.OK);
                        alert.setHeaderText(null);
                        alert.setTitle("Uspesno");
                        alert.showAndWait();
                        ((Stage) cenaLabel.getScene().getWindow()).close();
                    });
                });
    }
}
