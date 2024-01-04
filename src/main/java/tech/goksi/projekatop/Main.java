package tech.goksi.projekatop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @SuppressWarnings("ConstantConditions")
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/views/login-view.fxml"));
        Parent root = loginLoader.load();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        stage.setTitle("TabbyDelivery");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
