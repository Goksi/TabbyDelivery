package tech.goksi.projekatop.controllers;

import javafx.event.ActionEvent;
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

public class RegisterController implements DataStorageInjectable {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatedPasswordField;
    @FXML
    private Label errorLabel;
    private DataStorage storage;

    public void onRegisterClick(ActionEvent actionEvent) {
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
                    if (throwable instanceof KorisnikExistException) {
                        System.out.println("Postoji");
                    }
                })); /*TODO: poruka*/
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
    }
}
