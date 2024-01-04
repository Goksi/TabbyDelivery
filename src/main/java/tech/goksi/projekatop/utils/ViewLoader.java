package tech.goksi.projekatop.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Callback;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ViewLoader {
    private static final Logger LOGGER = Logger.getLogger(ViewLoader.class.getName());

    private ViewLoader() {
    }

    public static Parent load(String fxmlPath, Callback<Class<?>, Object> controllerFactory) {
        Parent parent = null;
        try {
            FXMLLoader loader = new FXMLLoader(ViewLoader.class.getResource(fxmlPath));
            loader.setControllerFactory(controllerFactory);
            parent = loader.load();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Greska u ucitavanju pogleda !", e);
        }

        return parent;
    }
}
