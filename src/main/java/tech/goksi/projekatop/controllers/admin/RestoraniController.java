package tech.goksi.projekatop.controllers.admin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import tech.goksi.projekatop.models.Restoran;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.persistance.DataStorageInjectable;
import tech.goksi.projekatop.utils.ImageUtils;

public class RestoraniController implements DataStorageInjectable {
    @FXML
    private ImageView logoView;
    @FXML
    private ListView<Restoran> restoraniListView;
    private final ObservableList<Restoran> restorani;
    private DataStorage storage;

    public RestoraniController() {
        restorani = FXCollections.observableArrayList();
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
            return;
        }
        if (restoran.getLogo() == null) {
            logoView.setImage(ImageUtils.textToImage(restoran.getNaziv(), (int) logoView.getFitWidth(), (int) logoView.getFitHeight()));
        } else logoView.setImage(restoran.getLogo());
    }

    private void populateListView() {
        storage.getAllRestorans()
                .thenAccept(restorans -> Platform.runLater(() -> restorani.setAll(restorans)));
    }
}
