package tech.goksi.projekatop.controllers.porudzbine;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Pagination;
import javafx.util.Callback;
import tech.goksi.projekatop.TabbyViews;
import tech.goksi.projekatop.models.Restoran;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.persistance.DataStorageInjectable;
import tech.goksi.projekatop.utils.ControllerFactory;
import tech.goksi.projekatop.utils.ViewLoader;

import java.util.List;

public class NovaPorudzbinaController implements DataStorageInjectable {
    private static final int RESTORANI_PER_PAGE = 4;
    private final ObservableList<Restoran> restorani;
    @FXML
    private Pagination pagination;
    private DataStorage storage;

    public NovaPorudzbinaController() {
        restorani = FXCollections.observableArrayList();
    }

    @SuppressWarnings("unchecked")
    public void initialize() {
        restorani.addListener((ListChangeListener<? super Restoran>) change -> {
            while (change.next()) {
                List<Restoran> sublist = (List<Restoran>) change.getAddedSubList();
                int numberOfPages = (int) Math.ceil((double) sublist.size() / RESTORANI_PER_PAGE);
                Platform.runLater(() -> {
                    pagination.setPageCount(numberOfPages);
                    pagination.setPageFactory(new TabbyPageFactory(sublist, storage));
                });
            }
        });
        storage.getAllRestorans()
                .thenAccept(restorani::setAll);
    }

    @Override
    public void setDataStorage(DataStorage storage) {
        this.storage = storage;
    }

    private record TabbyPageFactory(List<Restoran> restorani, DataStorage storage) implements Callback<Integer, Node> {

        @Override
        public Node call(Integer integer) {
            int start = integer * RESTORANI_PER_PAGE;
            int end = Math.min(start + RESTORANI_PER_PAGE, restorani.size());
            List<Restoran> forPage = restorani.subList(start, end);
            Parent parent = ViewLoader.load(TabbyViews.RESTORAN_LIST, clazz -> ControllerFactory.controllerForClass(clazz, storage, null));
            parent.setUserData(forPage);
            return parent;
        }
    }
}

