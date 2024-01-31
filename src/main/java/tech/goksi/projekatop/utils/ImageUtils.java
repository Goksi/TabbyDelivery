package tech.goksi.projekatop.utils;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    public static void centerImage(ImageView imageView) {
        Image image = imageView.getImage();
        if (image != null) {

            double ratioX = imageView.getFitWidth() / image.getWidth();
            double ratioY = imageView.getFitHeight() / image.getHeight();

            double coefficient = Math.min(ratioX, ratioY);

            double w = image.getWidth() * coefficient;
            double h = image.getHeight() * coefficient;

            imageView.setX((imageView.getFitWidth() - w) / 2);
            imageView.setY((imageView.getFitHeight() - h) / 2);
        }
    }
}
