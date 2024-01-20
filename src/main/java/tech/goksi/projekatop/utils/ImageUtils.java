package tech.goksi.projekatop.utils;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Font;

public final class ImageUtils {
    private ImageUtils() {
    }

    public static Image textToImage(String text, int width, int height) {
        Label label = new Label(text);
        label.setMinSize(width, height);
        label.setMaxSize(width, height);
        label.setPrefSize(width, height);
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);
        label.setFont(new Font("Arial", 40));
        WritableImage image = new WritableImage(width, height);
        Scene scene = new Scene(new Group(label));
        scene.snapshot(image);
        return image;
    }
}
