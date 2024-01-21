package tech.goksi.projekatop.controllers.admin;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private File newLogo;

    public UrediRestoranController() {
        restoranProperty = new SimpleObjectProperty<>();
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
            }
        });
        jelaListView.sceneProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                restoranProperty.set((Restoran) newValue.getRoot().getUserData());
            }
        });
    }

    @Override
    public void setDataStorage(DataStorage storage) {
        this.storage = storage;
    }

    public void onSacuvaj(ActionEvent actionEvent) {
    }

    public void onLogoClick(MouseEvent mouseEvent) {
        Window parentWindow = ((ImageView) mouseEvent.getSource()).getScene().getWindow();
        File file = ImagePicker.getInstance().open(parentWindow);
        if (file != null) {
            newLogo = file;
            logoView.setImage(new Image("file:///" + file.getAbsolutePath()));
        }
    }
}
