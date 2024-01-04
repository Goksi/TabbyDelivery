package tech.goksi.projekatop.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import tech.goksi.projekatop.exceptions.KorisnikExistException;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.persistance.DataStorageInjectable;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterController implements DataStorageInjectable {
    private static final Logger LOGGER = Logger.getLogger(RegisterController.class.getName());

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatedPasswordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Label successLabel;
    private DataStorage storage;

    public void onRegisterClick() {
        String username = usernameField.getText();
        String errorBorderStyle = "-fx-border-color: rgba(255, 0, 0, 0.2); -fx-border-width: 2px;";
        if (username.length() < 5) {
            usernameField.setStyle(errorBorderStyle);
            errorLabel.setText("Username mora da ima najmanje 5 karaktera !");
            return;
        }
        String password = passwordField.getText();
        String repeatedPassword = repeatedPasswordField.getText();
        if (!password.equals(repeatedPassword)) {
            passwordField.setStyle(errorBorderStyle);
            repeatedPasswordField.setStyle(errorBorderStyle);
            errorLabel.setText("Sifre se ne podudaraju !");
            return;
        }
        if (password.length() < 8) {
            passwordField.setStyle(errorBorderStyle);
            errorLabel.setText("Password mora da ima najmanje 8 karaktera !");
            return;
        }
        storage.addUser(username, password, false)
                .whenComplete(((unused, throwable) -> {
                    if (throwable != null) {
                        if (throwable.getCause() instanceof KorisnikExistException) {
                            Platform.runLater(() -> errorLabel.setText("Korisnik sa tim korisnickim imenom vec postoji !"));
                        } else LOGGER.log(Level.SEVERE, "Greska pri registrovanju korisnika !", throwable);
                    } else {
                        Platform.runLater(() -> successLabel.setText("Uspesno ste napravili nalog " + username + ", mozete se ulogovati !"));
                    }
                }));
    }

    public void onLoginClick(MouseEvent mouseEvent) {
        Parent parent = ((Label) mouseEvent.getSource()).getParent();
        StackPane stackPane = (StackPane) parent.getParent();
        stackPane.getChildren().get(1).setVisible(true);
        parent.setVisible(false);
    }

    @Override
    public void setDataStorage(DataStorage storage) {
        this.storage = storage;
    }

    public void onFieldWrite(KeyEvent keyEvent) {
        TextField sourceField = (TextField) keyEvent.getSource();
        sourceField.setStyle(null);
        errorLabel.setText("");
        successLabel.setText("");
    }
}
