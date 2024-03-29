package tech.goksi.projekatop;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.persistance.impl.SQLiteImpl;
import tech.goksi.projekatop.utils.ViewLoader;

public class Main extends Application {
    @SuppressWarnings("ConstantConditions")
    @Override
    public void start(Stage stage) throws Exception {
        DataStorage storage = new SQLiteImpl();
        Parent login = ViewLoader.load(TabbyViews.LOGIN, storage);
        Parent register = ViewLoader.load(TabbyViews.REGISTER, storage);
        stage.setScene(new Scene(new StackPane(register, login)));
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        stage.setTitle("TabbyDelivery");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
