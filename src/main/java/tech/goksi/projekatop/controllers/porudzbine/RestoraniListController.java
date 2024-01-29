package tech.goksi.projekatop.controllers.porudzbine;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import tech.goksi.projekatop.TabbyViews;
import tech.goksi.projekatop.models.Restoran;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.utils.Injectable;
import tech.goksi.projekatop.utils.ViewLoader;

import java.util.List;

public class RestoraniListController implements Injectable {
    private final DataStorage storage;
    private final List<Restoran> restorani;
    @FXML
    private GridPane pane;

    public RestoraniListController(DataStorage storage, List<Restoran> restorani) {
        this.storage = storage;
        this.restorani = restorani;
    }

    public void initialize() {
        int row = 0, col = 0;
        for (Restoran restoran : restorani) {
            Parent parent = ViewLoader.load(TabbyViews.RESTORAN_KARTICA, storage, restoran); //TODO dal mi treba storage ovde
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
}
