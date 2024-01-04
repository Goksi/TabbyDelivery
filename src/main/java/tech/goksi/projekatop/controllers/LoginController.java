package tech.goksi.projekatop.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;


    public void onLoginClick() {
        String username = usernameField.getText();
        String errorBorderStyle = "-fx-border-color: rgba(255, 0, 0, 0.2); -fx-border-width: 2px;";
        if (username.length() < 5) {
            usernameField.setStyle(errorBorderStyle);
            errorLabel.setText("Username mora da ima najmanje 5 karaktera !");
            return;
        }
        String password = passwordField.getText();
        if (password.length() < 8) {
            passwordField.setStyle(errorBorderStyle);
            errorLabel.setText("Password mora da ima najmanje 8 karaktera !");
            return;
        }
    }

    /*Ovde cemo resetovati greske*/
    public void onFieldWrite(KeyEvent keyEvent) {
        TextField sourceField = (TextField) keyEvent.getSource();
        sourceField.setStyle(null);
        errorLabel.setText("");
    }
}
