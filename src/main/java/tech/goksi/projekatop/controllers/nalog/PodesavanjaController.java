package tech.goksi.projekatop.controllers.nalog;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyEvent;
import tech.goksi.projekatop.models.Korisnik;
import tech.goksi.projekatop.models.KorisnikInjectable;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.persistance.DataStorageInjectable;

public class PodesavanjaController implements DataStorageInjectable, KorisnikInjectable {
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatedPasswordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Label infoLabel;
    private Korisnik currentUser;
    private DataStorage storage;

    public void initialize() {
        infoLabel.setText(String.format(
                infoLabel.getText(), currentUser.getUsername(), currentUser.isAdmin() ? "DA" : "NE", currentUser.getDatumRegistracije(), 1
        )); // TODO
    }

    @Override
    public void setModel(Korisnik korisnik) {
        this.currentUser = korisnik;
    }

    @Override
    public void setDataStorage(DataStorage storage) {
        this.storage = storage;
    }

    public void onPromeniLoznikuClick(ActionEvent actionEvent) {
    }

    public void onFieldWrite(KeyEvent keyEvent) {
    }
}
