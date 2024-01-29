package tech.goksi.projekatop.controllers.porudzbine;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import tech.goksi.projekatop.TabbyViews;
import tech.goksi.projekatop.models.Restoran;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.persistance.DataStorageInjectable;
import tech.goksi.projekatop.utils.ControllerFactory;
import tech.goksi.projekatop.utils.ImageUtils;
import tech.goksi.projekatop.utils.ViewLoader;

import java.util.Objects;

public class RestoranKarticaController implements DataStorageInjectable {
    private final SimpleObjectProperty<Restoran> restoranProperty;
    @FXML
    private AnchorPane pane;
    @FXML
    private ImageView logoView;
    @FXML
    private Label infoLabel;
    private DataStorage storage;

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
        Pane rootPane = (Pane) pane.getScene().lookup("#rootPane");
        var children = rootPane.getChildren();
        Runnable backer = () -> rootPane.getChildren().setAll(children);
        Parent parent = ViewLoader.load(TabbyViews.PORUDZBINA, clazz -> ControllerFactory.controllerForClass(clazz, storage, null));
    }

    @Override
    public void setDataStorage(DataStorage storage) {
        this.storage = storage;
    }
}
