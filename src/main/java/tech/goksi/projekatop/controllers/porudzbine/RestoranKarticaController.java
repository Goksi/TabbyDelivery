package tech.goksi.projekatop.controllers.porudzbine;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tech.goksi.projekatop.TabbyViews;
import tech.goksi.projekatop.models.Restoran;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.utils.ImageUtils;
import tech.goksi.projekatop.utils.Injectable;
import tech.goksi.projekatop.utils.ViewLoader;

import java.util.Objects;

public class RestoranKarticaController implements Injectable {
    private static boolean otvoren = false;
    private final Restoran restoran;
    private final DataStorage storage;
    @FXML
    private ImageView logoView;
    @FXML
    private Label infoLabel;


    public RestoranKarticaController(DataStorage storage, Restoran restoran) {
        this.storage = storage;
        this.restoran = restoran;
    }

    public void initialize() {
        String pattern = infoLabel.getText();
        infoLabel.setText(pattern.formatted(restoran.getNaziv(), restoran.getAdresa()));
        Image logo = restoran.getLogo();
        logoView.setImage(Objects.requireNonNullElseGet(logo, () -> ImageUtils.textToImage("Nema\nslike", (int) logoView.getFitWidth(), (int) logoView.getFitHeight())));
    }

    @SuppressWarnings("ConstantConditions")
    public void onClick() {
        if (otvoren) return;
        if (restoran.getJela().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Ovaj restoran nema ni jedno jelo, nemoguce je napraviti porudzbinu", ButtonType.OK);
            alert.setHeaderText(null);
            alert.setTitle("Upozorenje");
            alert.show();
            return;
        }
        Parent parent = ViewLoader.load(TabbyViews.PORUDZBINA, storage, restoran);
        Stage stage = new Stage();
        stage.setTitle("Naruci iz restorana " + restoran.getNaziv());
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        stage.setScene(new Scene(parent));
        otvoren = true;
        stage.showAndWait();
        otvoren = false;
    }

}
