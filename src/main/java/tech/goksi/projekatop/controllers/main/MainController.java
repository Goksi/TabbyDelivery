package tech.goksi.projekatop.controllers.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import tech.goksi.projekatop.TabbyViews;
import tech.goksi.projekatop.models.Korisnik;
import tech.goksi.projekatop.paginating.PageNavigator;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.utils.Injectable;
import tech.goksi.projekatop.utils.ViewLoader;

public class MainController implements Injectable {
    private final DataStorage storage;
    private final Korisnik currentUser;
    private PageNavigator pageNavigator;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu adminMenu;
    @FXML
    private Menu mojNalogMenu;
    @FXML
    private Pane contentPane;

    public MainController(DataStorage storage, Korisnik currentUser) {
        this.storage = storage;
        this.currentUser = currentUser;
    }

    public void initialize() {
        if (!currentUser.isAdmin()) adminMenu.setVisible(false);
        MenuItem welcomeBackItem = mojNalogMenu.getItems().getFirst();
        welcomeBackItem.setText(String.format(welcomeBackItem.getText(), currentUser.getUsername()));
        pageNavigator = new PageNavigator(contentPane, storage, currentUser);
    }

    public void onLogoutClick() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Da li ste sigurni da zelite da se izlogujete ?", ButtonType.YES, ButtonType.NO);
        confirmation.setHeaderText(null);
        confirmation.setTitle("Logout");
        confirmation.showAndWait();
        if (confirmation.getResult() == ButtonType.NO) return;
        Parent login = ViewLoader.load(TabbyViews.LOGIN, storage);
        Parent register = ViewLoader.load(TabbyViews.REGISTER, storage);
        Stage mainStage = (Stage) menuBar.getScene().getWindow();
        mainStage.setScene(new Scene(new StackPane(register, login)));
    }

    public void onMenuItemClick(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof MenuItem menuItem) {
            pageNavigator.goToPage(menuItem);
        }
    }
}
