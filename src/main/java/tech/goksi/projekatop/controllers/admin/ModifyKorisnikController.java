package tech.goksi.projekatop.controllers.admin;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import tech.goksi.projekatop.controllers.main.RegisterController;
import tech.goksi.projekatop.exceptions.KorisnikExistException;
import tech.goksi.projekatop.models.Korisnik;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.utils.EncryptionUtils;
import tech.goksi.projekatop.utils.Injectable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModifyKorisnikController implements Injectable {
    private static final Logger LOGGER = Logger.getLogger(RegisterController.class.getName());
    private final DataStorage storage;
    private final Korisnik korisnik;

    @FXML
    private TextField idField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private CheckBox adminCheckbox;
    @FXML
    private Button dodajKorisnikaButton;
    @FXML
    private Label errorLabel;
    @FXML
    private Label successLabel;


    public ModifyKorisnikController(DataStorage storage, Korisnik korisnik) {
        this.storage = storage;
        this.korisnik = korisnik;
    }

    public void initialize() {
        if (korisnik != null) {
            dodajKorisnikaButton.setText("Uredi korisnika");
            idField.setText(Integer.toString(korisnik.getId()));
            usernameField.setText(korisnik.getUsername());
            adminCheckbox.selectedProperty().set(korisnik.isAdmin());
        }
    }

    private void onDodajKorisnika() {
        String username = usernameField.getText();
        if (username.length() < 5) {
            usernameField.getStyleClass().add("error-field");
            errorLabel.setText("Username mora da ima 5 karaktera !");
            return;
        }
        String password = passwordField.getText();
        if (password.length() < 8) {
            passwordField.getStyleClass().add("error-field");
            errorLabel.setText("Password mora da ima 8 karaktera !");
            return;
        }
        storage.addUser(username, password, adminCheckbox.isSelected())
                .whenComplete(((unused, throwable) -> {
                    if (throwable != null) {
                        if (throwable.getCause() instanceof KorisnikExistException) {
                            Platform.runLater(() -> {
                                errorLabel.setText("Korisnik sa tim imenom vec postoji !");
                                usernameField.getStyleClass().add("error-field");
                            });
                        } else LOGGER.log(Level.SEVERE, "Greska pri dodavanju korisnika !", throwable);
                    } else Platform.runLater(() -> successLabel.setText("Uspesno ste dodali novog korisnika !"));
                }));
    }

    private void onUrediKorisnika() {
        Map<String, Object> changedFields = new HashMap<>();
        String username = usernameField.getText();
        if (!username.equals(korisnik.getUsername())) {
            if (username.length() < 5) {
                usernameField.getStyleClass().add("error-field");
                errorLabel.setText("Username mora da ima 5 karaktera !");
                return;
            } else changedFields.put("username", username);
        }
        String password = passwordField.getText();
        if (!password.isEmpty()) {
            if (password.length() < 8) {
                passwordField.getStyleClass().add("error-field");
                errorLabel.setText("Password mora da ima 8 karaktera !");
                return;
            } else changedFields.put("password", EncryptionUtils.createHash(password));
        }
        if (korisnik.isAdmin() != adminCheckbox.isSelected())
            changedFields.put("admin", adminCheckbox.isSelected());

        storage.modifyUser(korisnik, changedFields)
                .whenComplete(((unused, throwable) -> {
                    if (throwable != null) {
                        if (throwable.getCause() instanceof KorisnikExistException) {
                            Platform.runLater(() -> {
                                errorLabel.setText("Korisnik sa tim imenom vec postoji !");
                                usernameField.getStyleClass().add("error-field");
                            });
                        } else LOGGER.log(Level.SEVERE, "Greska pri modifikovanju korisnika !", throwable);
                    } else
                        Platform.runLater(() -> successLabel.setText("Uspesno ste uredili korisnika " + korisnik.getUsername() + " !"));
                }));
    }

    public void onButtonAction() {
        if (korisnik == null) {
            onDodajKorisnika();
        } else {
            onUrediKorisnika();
        }
    }

    public void onFieldWrite(KeyEvent keyEvent) {
        TextField sourceField = (TextField) keyEvent.getSource();
        sourceField.getStyleClass().remove("error-field");
        errorLabel.setText("");
        successLabel.setText("");
    }
}
