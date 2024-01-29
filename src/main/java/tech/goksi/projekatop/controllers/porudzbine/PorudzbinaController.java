package tech.goksi.projekatop.controllers.porudzbine;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import tech.goksi.projekatop.models.NarucenoJelo;
import tech.goksi.projekatop.models.PorudzbinaMaker;
import tech.goksi.projekatop.models.Restoran;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.utils.Injectable;

public class PorudzbinaController implements Injectable {
    private final PorudzbinaMaker trenutnaPorudzbina;
    private final Restoran restoran;
    private final DataStorage storage;
    private String cenaPattern;
    @FXML
    private ListView<NarucenoJelo> racunListView;
    @FXML
    private Label cenaLabel;
    @FXML
    private GridPane gridPane;

    public PorudzbinaController(DataStorage storage, Restoran restoran) {
        trenutnaPorudzbina = new PorudzbinaMaker();
        this.storage = storage;
        this.restoran = restoran;
    }

    public void initialize() {
        racunListView.itemsProperty().bind(trenutnaPorudzbina.narucenaJelaProperty());
        cenaPattern = cenaLabel.getText();
        trenutnaPorudzbina.ukupnaCenaPropertyProperty().addListener((obs, oldValue, newValue) -> {
            cenaLabel.setText(cenaPattern.formatted(newValue));
        });
        // TODO staviti jela
    }

}
