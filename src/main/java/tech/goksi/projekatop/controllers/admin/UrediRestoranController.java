package tech.goksi.projekatop.controllers.admin;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import tech.goksi.projekatop.models.Jelo;
import tech.goksi.projekatop.models.Restoran;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.persistance.DataStorageInjectable;
import tech.goksi.projekatop.utils.ImagePicker;
import tech.goksi.projekatop.utils.ImageUtils;

import java.io.File;

/*TODO: jela cell i slika na njima*/
public class UrediRestoranController implements DataStorageInjectable {
    private final SimpleObjectProperty<Restoran> restoranProperty;
    @FXML
    private ImageView logoView;
    @FXML
    private TextField nazivTextField;
    @FXML
    private TextField adresaTextField;
    @FXML
    private ListView<Jelo> jelaListView;
    private DataStorage storage;
    private SimpleObjectProperty<File> newLogoProperty;
    private boolean logoChanged = false;

    public UrediRestoranController() {
        restoranProperty = new SimpleObjectProperty<>();
        newLogoProperty = new SimpleObjectProperty<>();
    }

    public void initialize() {
        jelaListView.setCellFactory(jelaView -> {
            ListCell<Jelo> cell = new ListCell<>() {
                @Override
                protected void updateItem(Jelo jelo, boolean empty) {
                    super.updateItem(jelo, empty);
                    if (empty || jelo == null) {
                        setText(null);
                    } else {
                        setText(jelo.getNaziv());
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        restoranProperty.addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                jelaListView.getItems().setAll(newValue.getJela());
                nazivTextField.setText(newValue.getNaziv());
                adresaTextField.setText(newValue.getAdresa());
                if (newValue.getLogo() == null) {
                    logoView.setImage(ImageUtils.textToImage("Nema\nslike", (int) logoView.getFitWidth(), (int) logoView.getFitHeight()));
                } else logoView.setImage(newValue.getLogo());
                logoChanged = true;
            }
        });
        newLogoProperty.addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                logoView.setImage(ImageUtils.textToImage("Nema\nslike", (int) logoView.getFitWidth(), (int) logoView.getFitHeight()));
            } else logoView.setImage(new Image("file:///" + newValue.getAbsolutePath()));
        });
        jelaListView.sceneProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                restoranProperty.set((Restoran) newValue.getRoot().getUserData());
            }
        });
        ContextMenu contextMenu = new ContextMenu();
        MenuItem obrisiLogo = new MenuItem("Obrisi logo");
        obrisiLogo.addEventHandler(ActionEvent.ACTION, this::onDeleteLogo);
        contextMenu.getItems().add(obrisiLogo);

        logoView.setOnContextMenuRequested(contextMenuEvent -> {
            contextMenu.show(logoView, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
        });
    }

    @Override
    public void setDataStorage(DataStorage storage) {
        this.storage = storage;
    }

    public void onSacuvaj(ActionEvent actionEvent) {
    }

    public void onDeleteLogo(ActionEvent actionEvent) {
        newLogoProperty.set(null);
    }

    public void onLogoClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() != MouseButton.PRIMARY) return;
        Window parentWindow = ((ImageView) mouseEvent.getSource()).getScene().getWindow();
        File file = ImagePicker.getInstance().open(parentWindow);
        if (file != null) {
            newLogoProperty.set(file);
        }
    }
}
