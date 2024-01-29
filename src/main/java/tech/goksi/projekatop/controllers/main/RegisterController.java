package tech.goksi.projekatop.controllers.main;

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
import tech.goksi.projekatop.utils.Injectable;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterController implements Injectable {
    private static final Logger LOGGER = Logger.getLogger(RegisterController.class.getName());
    private final DataStorage storage;

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

    public RegisterController(DataStorage storage) {
        this.storage = storage;
    }

    public void onRegisterClick() {
        String username = usernameField.getText();
        if (username.length() < 5) {
            usernameField.getStyleClass().add("error-field");
            errorLabel.setText("Username mora da ima najmanje 5 karaktera !");
            return;
        }
        String password = passwordField.getText();
        String repeatedPassword = repeatedPasswordField.getText();
        if (password.length() < 8) {
            passwordField.getStyleClass().add("error-field");
            errorLabel.setText("Password mora da ima najmanje 8 karaktera !");
            return;
        }
        if (!password.equals(repeatedPassword)) {
            repeatedPasswordField.getStyleClass().add("error-field");
            errorLabel.setText("Sifre se ne podudaraju !");
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

    public void onFieldWrite(KeyEvent keyEvent) {
        TextField sourceField = (TextField) keyEvent.getSource();
        sourceField.getStyleClass().remove("error-field");
        errorLabel.setText("");
        successLabel.setText("");
    }
}
