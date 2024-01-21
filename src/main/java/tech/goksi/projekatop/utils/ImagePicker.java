package tech.goksi.projekatop.utils;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class ImagePicker {
    private static ImagePicker INSTANCE;
    private final FileChooser fileChooser;

    private ImagePicker() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Otvorite sliku");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Slika (*.bmp, *.gif, *.jpeg, *.png)", "*.bmp", "*.gif", "*.jpeg", "*.png"));
    }

    public File open(Window parent) {
        return fileChooser.showOpenDialog(parent);
    }

    public static ImagePicker getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ImagePicker();
        }
        return INSTANCE;
    }
}
