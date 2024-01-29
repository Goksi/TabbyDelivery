package tech.goksi.projekatop.controllers.nalog;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyEvent;
import tech.goksi.projekatop.models.Korisnik;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.utils.Injectable;

public class PodesavanjaController implements Injectable {
    private final DataStorage storage;
    private Korisnik currentUser;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatedPasswordField;
    @FXML
    private Label successLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private Label infoLabel;

    public PodesavanjaController(DataStorage storage, Korisnik currentUser) {
        this.currentUser = currentUser;
        this.storage = storage;
    }

    public void initialize() {
        infoLabel.setText(String.format(
                infoLabel.getText(), currentUser.getUsername(), currentUser.isAdmin() ? "DA" : "NE", currentUser.getDatumRegistracije(), 1
        )); // TODO
    }

    public void onPromeniLoznikuClick() {
        String password = passwordField.getText();
        if (password.length() < 8) {
            passwordField.getStyleClass().add("error-field");
            errorLabel.setText("Sifra mora da ima najmanje 8 karaktera !");
            return;
        }
        String repeatedPassword = repeatedPasswordField.getText();
        if (!password.equals(repeatedPassword)) {
            repeatedPasswordField.getStyleClass().add("error-field");
            errorLabel.setText("Sifre se ne podudaraju !");
            return;
        }
        if (currentUser.tryLogin(password)) {
            errorLabel.setText("Nova lozinka ne moze biti ista kao stara !");
            return;
        }
        storage.changePassword(currentUser, password)
                .thenAccept(korisnik -> currentUser = korisnik)
                .thenRun(() -> Platform.runLater(() -> {
                    passwordField.setText("");
                    repeatedPasswordField.setText("");
                    successLabel.setText("Uspesno promenjena sifra !");
                }));
    }

    public void onFieldWrite(KeyEvent keyEvent) {
        PasswordField passwordField = (PasswordField) keyEvent.getSource();
        passwordField.getStyleClass().remove("error-field");
        errorLabel.setText("");
        successLabel.setText("");
    }
}
