package tech.goksi.projekatop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/views/login-view.fxml"));
        stage.setResizable(false);
        stage.setScene(loginLoader.load());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
