package tech.goksi.projekatop.controllers.admin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tech.goksi.projekatop.TabbyViews;
import tech.goksi.projekatop.models.Restoran;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.persistance.DataStorageInjectable;
import tech.goksi.projekatop.utils.ControllerFactory;
import tech.goksi.projekatop.utils.ImageUtils;
import tech.goksi.projekatop.utils.ViewLoader;

import java.text.MessageFormat;

public class RestoraniController implements DataStorageInjectable {
    private final ObservableList<Restoran> restorani;
    private final String labelFormat;
    @FXML
    private Button urediRestoranBtn;
    @FXML
    private ImageView logoView;
    @FXML
    private Label infoLabel;
    @FXML
    private ListView<Restoran> restoraniListView;
    private DataStorage storage;


    public RestoraniController() {
        restorani = FXCollections.observableArrayList();
        labelFormat = "Naziv: {0}\nAdresa: {1}\nBroj jela: {2}";
    }

    public void initialize() {
        restoraniListView.setCellFactory(restoraniView -> {
            ListCell<Restoran> cell = new ListCell<>() {
                @Override
                protected void updateItem(Restoran restoran, boolean empty) {
                    super.updateItem(restoran, empty);
                    if (empty || restoran == null) {
                        setText(null);
                    } else {
                        setText(restoran.getNaziv());
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);

            return cell;
        });
        restoraniListView.getSelectionModel().selectedItemProperty()
                .addListener(((observableValue, oldValue, newValue) -> onSelection(newValue)));
        restoraniListView.setItems(restorani);

        populateListView();
    }

    @Override
    public void setDataStorage(DataStorage storage) {
        this.storage = storage;
    }


    public void onSelection(Restoran restoran) {
        if (restoran == null) {
            logoView.setImage(null);
            infoLabel.setText(null);
            urediRestoranBtn.setVisible(false);
            return;
        }
        urediRestoranBtn.setVisible(true);
        if (restoran.getLogo() == null) {
            logoView.setImage(ImageUtils.textToImage("Nema\nslike", (int) logoView.getFitWidth(), (int) logoView.getFitHeight()));
        } else logoView.setImage(restoran.getLogo());
        infoLabel.setText(MessageFormat.format(labelFormat, restoran.getNaziv(), restoran.getAdresa(), restoran.getJela().size()));
    }

    @SuppressWarnings("ConstantConditions")
    public void onDodajRestoran(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);
        Stage stage = new Stage();
        stage.setTitle("Dodaj restoran");
        Parent parent = ViewLoader.load(TabbyViews.DODAJ_RESTORAN, clazz -> ControllerFactory.controllerForClass(clazz, storage, null));
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        stage.setScene(new Scene(parent));
        stage.showAndWait();
        populateListView();
        button.setDisable(false);
    }

    public void onUrediRestoran(ActionEvent actionEvent) {
    }

    private void populateListView() {
        storage.getAllRestorans()
                .thenAccept(restorans -> Platform.runLater(() -> restorani.setAll(restorans)));
    }
}
