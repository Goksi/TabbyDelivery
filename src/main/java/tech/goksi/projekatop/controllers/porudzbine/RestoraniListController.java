package tech.goksi.projekatop.controllers.porudzbine;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import tech.goksi.projekatop.TabbyViews;
import tech.goksi.projekatop.models.Restoran;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.persistance.DataStorageInjectable;
import tech.goksi.projekatop.utils.ControllerFactory;
import tech.goksi.projekatop.utils.ViewLoader;

import java.util.List;

public class RestoraniListController implements DataStorageInjectable {
    @FXML
    private GridPane pane;
    private DataStorage storage;


    @SuppressWarnings("unchecked")
    public void initialize() {
        pane.sceneProperty().addListener(((obs, oldValue, newValue) -> {
            if (newValue == null) return;
            List<Restoran> restorani = (List<Restoran>) pane.getUserData();
            int row = 0, col = 0;
            for (Restoran restoran : restorani) {
                Parent parent = ViewLoader.load(TabbyViews.RESTORAN_KARTICA, clazz -> ControllerFactory.controllerForClass(clazz, storage, null)); //TODO dal mi treba storage ovde
                parent.setUserData(restoran);
                int tempCol = col;
                int tempRow = row;
                Platform.runLater(() -> pane.add(parent, tempCol, tempRow));
                col++;
                if (col == 2) {
                    col = 0;
                    row++;
                }
            }
        }
        ));
    }

    @Override
    public void setDataStorage(DataStorage storage) {
        this.storage = storage;
    }
}
