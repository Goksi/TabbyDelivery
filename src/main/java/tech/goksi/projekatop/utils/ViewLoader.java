package tech.goksi.projekatop.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import tech.goksi.projekatop.TabbyViews;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ViewLoader {
    private static final Logger LOGGER = Logger.getLogger(ViewLoader.class.getName());
    @SuppressWarnings("ConstantConditions")
    private static final String STYLESHEETS = ViewLoader.class.getResource("/styles/style.css").toExternalForm();

    private ViewLoader() {
    }

    public static Parent load(TabbyViews view, Object... args) {
        Parent parent = null;
        try {
            FXMLLoader loader = new FXMLLoader(ViewLoader.class.getResource(view.toString()));
            loader.setControllerFactory(clazz -> ControllerFactory.controllerForClass(clazz, args));
            parent = loader.load();
            parent.getStylesheets().add(STYLESHEETS);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Greska u ucitavanju pogleda !", e);
        }
        return parent;
    }
}
