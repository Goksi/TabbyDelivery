package tech.goksi.projekatop.controllers.porudzbine;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import tech.goksi.projekatop.models.Restoran;
import tech.goksi.projekatop.utils.ImageUtils;

import java.util.Objects;

public class RestoranKarticaController {
    private final SimpleObjectProperty<Restoran> restoranProperty;
    @FXML
    private AnchorPane pane;
    @FXML
    private ImageView logoView;
    @FXML
    private Label infoLabel;

    public RestoranKarticaController() {
        restoranProperty = new SimpleObjectProperty<>();
    }

    public void initialize() {
        String pattern = infoLabel.getText();

        restoranProperty.addListener((obs, oldValue, newValue) -> {
            if (newValue == null) return;
            infoLabel.setText(pattern.formatted(newValue.getNaziv(), newValue.getAdresa()));
            Image logo = newValue.getLogo();
            logoView.setImage(Objects.requireNonNullElseGet(logo, () -> ImageUtils.textToImage("Nema\nslike", (int) logoView.getFitWidth(), (int) logoView.getFitHeight())));
        });
        pane.sceneProperty().addListener(((obs, oldValue, newValue) -> {
            if (newValue == null) return;
            restoranProperty.set((Restoran) pane.getUserData());
        }));
    }

    public void onClick(MouseEvent mouseEvent) {
        
    }
}
