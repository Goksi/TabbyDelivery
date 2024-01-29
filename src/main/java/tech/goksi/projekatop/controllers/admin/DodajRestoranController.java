package tech.goksi.projekatop.controllers.admin;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import tech.goksi.projekatop.exceptions.RestoranExistException;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.utils.ImagePicker;
import tech.goksi.projekatop.utils.Injectable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DodajRestoranController implements Injectable {
    private static final Logger LOGGER = Logger.getLogger(DodajRestoranController.class.getName());
    private final DataStorage storage;
    @FXML
    private Label errorLabel;
    @FXML
    private Label successLabel;
    @FXML
    private TextField nazivField;
    @FXML
    private TextField adresaField;
    @FXML
    private Label closeLogoLabel;
    @FXML
    private Label imageLabel;
    private SimpleObjectProperty<File> logoProperty;

    public DodajRestoranController(DataStorage storage) {
        this.storage = storage;
    }

    public void initialize() {
        logoProperty = new SimpleObjectProperty<>();
        logoProperty.addListener(((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                imageLabel.setText(newValue.getAbsolutePath());
                closeLogoLabel.setVisible(true);
            } else {
                imageLabel.setText("");
                closeLogoLabel.setVisible(false);
            }
        }));
    }

    public void onPretraziLogo(ActionEvent actionEvent) {
        Window parentWindow = ((Button) actionEvent.getSource()).getScene().getWindow();
        File file = ImagePicker.getInstance().open(parentWindow);
        if (file == null) return;
        logoProperty.setValue(file);
    }

    public void onDodaj() {
        String naziv = nazivField.getText();
        if (naziv.length() < 5) {
            nazivField.getStyleClass().add("error-field");
            errorLabel.setText("Naziv mora imati makar 5 karaktera !");
            return;
        }
        String adresa = adresaField.getText();
        if (adresa.length() < 5) {
            adresaField.getStyleClass().add("error-field");
            errorLabel.setText("Adresa mora imati makar 5 karaktera !");
            return;
        }
        InputStream logo = null;
        try {
            File file = logoProperty.get();
            if (file != null) {
                logo = new FileInputStream(file);
            }
        } catch (FileNotFoundException e) {
            errorLabel.setText("Fajl za logo nije pronadjen !");
        }
        storage.addRestoran(naziv, adresa, logo)
                .whenComplete(((unused, throwable) -> {
                    if (throwable != null) {
                        if (throwable.getCause() instanceof RestoranExistException) {
                            Platform.runLater(() -> errorLabel.setText("Restoran sa tim imenom vec postoji ! !"));
                        } else LOGGER.log(Level.SEVERE, "Greska pri dodavanju restorana ! !", throwable);
                    } else {
                        Platform.runLater(() -> successLabel.setText("Uspesno ste dodali novi restoran !"));
                    }
                }));
    }

    public void onLogoClose(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            logoProperty.setValue(null);
        }
    }

    public void onFieldWrite(KeyEvent keyEvent) {
        TextField field = (TextField) keyEvent.getSource();
        field.getStyleClass().remove("error-field");
        errorLabel.setText("");
        successLabel.setText("");
    }
}
