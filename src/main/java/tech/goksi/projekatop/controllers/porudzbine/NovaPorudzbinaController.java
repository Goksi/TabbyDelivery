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

public class NovaPorudzbinaController implements DataStorageInjectable {
    @FXML
    private GridPane pane;
    private DataStorage storage;

    public void initialize() {
        storage.getAllRestorans()
                .thenAccept(restorani -> {
                    int col = 0, row = 0;
                    for (Restoran restoran : restorani) {
                        Parent parent = ViewLoader.load(TabbyViews.RESTORAN_KARTICA, clazz -> ControllerFactory.controllerForClass(clazz, storage, null)); //TODO dal mi treba storage ovde
                        parent.setUserData(restoran);
                        int tempCol = col;
                        int tempRow = row;
                        Platform.runLater(() -> pane.add(parent, tempCol, tempRow));
                        col++;
                        if (col == 4) {
                            col = 0;
                            row++;
                        }
                    }
                });
    }

    @Override
    public void setDataStorage(DataStorage storage) {
        this.storage = storage;
    }
}
