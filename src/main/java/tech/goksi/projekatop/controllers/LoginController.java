package tech.goksi.projekatop.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;


    public void onLoginClick(ActionEvent actionEvent) {
        String username = usernameField.getText();
    }
}
