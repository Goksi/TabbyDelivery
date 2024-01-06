package tech.goksi.projekatop.controllers.main;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import tech.goksi.projekatop.TabbyViews;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.persistance.DataStorageInjectable;
import tech.goksi.projekatop.utils.ControllerFactory;
import tech.goksi.projekatop.utils.ViewLoader;

public class LoginController implements DataStorageInjectable {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    private DataStorage storage;


    public void onLoginClick(ActionEvent actionEvent) {
        String username = usernameField.getText();
        if (username.length() < 5) {
            usernameField.getStyleClass().add("error-field");
            errorLabel.setText("Username mora da ima najmanje 5 karaktera !");
            return;
        }
        String password = passwordField.getText();
        if (password.length() < 8) {
            passwordField.getStyleClass().add("error-field");
            errorLabel.setText("Password mora da ima najmanje 8 karaktera !");
            return;
        }
        ((Control) actionEvent.getSource()).setDisable(true);
        storage.findUserByUsername(username)
                .thenAccept(korisnik -> {
                    if (korisnik == null || !korisnik.tryLogin(password)) {
                        Platform.runLater(() -> errorLabel.setText("Pogresan username ili sifra !"));
                        return;
                    }
                    /*TODO: bilo koji drugi slucaj, uspesan login, ne svidja mi se ovako*/
                    Parent mainWindow = ViewLoader.load(TabbyViews.MAIN,
                            clazz -> ControllerFactory.controllerForClass(clazz, storage, korisnik));
                    Stage mainStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    Platform.runLater(() -> mainStage.setScene(new Scene(mainWindow)));

                })
                .thenRun(() -> Platform.runLater(() -> ((Control) actionEvent.getSource()).setDisable(false)));
    }

    /*Ovde cemo resetovati greske*/
    public void onFieldWrite(KeyEvent keyEvent) {
        TextField sourceField = (TextField) keyEvent.getSource();
        sourceField.getStyleClass().remove("error-field");
        errorLabel.setText("");
    }

    public void onRegisterClick(MouseEvent mouseEvent) {
        Parent parent = ((Label) mouseEvent.getSource()).getParent();
        StackPane stackPane = (StackPane) parent.getParent();
        stackPane.getChildren().get(0).setVisible(true);
        parent.setVisible(false);
    }

    @Override
    public void setDataStorage(DataStorage storage) {
        this.storage = storage;
    }
}
