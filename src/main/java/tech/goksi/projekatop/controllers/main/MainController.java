package tech.goksi.projekatop.controllers.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import tech.goksi.projekatop.TabbyViews;
import tech.goksi.projekatop.models.Korisnik;
import tech.goksi.projekatop.models.KorisnikInjectable;
import tech.goksi.projekatop.paginating.PageNavigator;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.persistance.DataStorageInjectable;
import tech.goksi.projekatop.utils.ControllerFactory;
import tech.goksi.projekatop.utils.ViewLoader;

public class MainController implements DataStorageInjectable, KorisnikInjectable {
    private PageNavigator pageNavigator;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu adminMenu;
    @FXML
    private Menu mojNalogMenu;
    @FXML
    private SubScene contentScene;
    private DataStorage storage;
    private Korisnik currentUser;

    public void initialize() {
        if (!currentUser.isAdmin()) adminMenu.setVisible(false);
        MenuItem welcomeBackItem = mojNalogMenu.getItems().getFirst();
        welcomeBackItem.setText(String.format(welcomeBackItem.getText(), currentUser.getUsername()));
        pageNavigator = new PageNavigator(contentScene, storage, currentUser);
    }

    @Override
    public void setDataStorage(DataStorage storage) {
        this.storage = storage;
    }

    @Override
    public void setModel(Korisnik korisnik) {
        this.currentUser = korisnik;
    }

    public void onLogoutClick() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Da li ste sigurni da zelite da se izlogujete ?", ButtonType.YES, ButtonType.NO);
        confirmation.setHeaderText(null);
        confirmation.setTitle("Logout");
        confirmation.showAndWait();
        if (confirmation.getResult() == ButtonType.NO) return;
        Parent login = ViewLoader.load(TabbyViews.LOGIN,
                clazz -> ControllerFactory.controllerForClass(clazz, storage, null));
        Parent register = ViewLoader.load(TabbyViews.REGISTER,
                clazz -> ControllerFactory.controllerForClass(clazz, storage, null));
        Stage mainStage = (Stage) menuBar.getScene().getWindow();
        mainStage.setScene(new Scene(new StackPane(register, login)));
    }

    public void onMenuItemClick(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof MenuItem menuItem) {
            pageNavigator.goToPage(menuItem);
        }
    }
}
