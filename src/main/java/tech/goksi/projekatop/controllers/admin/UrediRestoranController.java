package tech.goksi.projekatop.controllers.admin;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import tech.goksi.projekatop.TabbyViews;
import tech.goksi.projekatop.exceptions.RestoranExistException;
import tech.goksi.projekatop.models.Jelo;
import tech.goksi.projekatop.models.Restoran;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.utils.ImagePicker;
import tech.goksi.projekatop.utils.ImageUtils;
import tech.goksi.projekatop.utils.Injectable;
import tech.goksi.projekatop.utils.ViewLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class UrediRestoranController implements Injectable {
    private final Restoran restoran;
    private final DataStorage storage;
    private File newLogo;
    @FXML
    private Label successLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private ImageView logoView;
    @FXML
    private TextField nazivTextField;
    @FXML
    private TextField adresaTextField;
    @FXML
    private ListView<Jelo> jelaListView;
    private boolean logoChanged = false;

    public UrediRestoranController(DataStorage storage, Restoran restoran) {
        this.restoran = restoran;
        this.storage = storage;
    }

    public void initialize() {
        jelaListView.setCellFactory(jelaView -> {
            ListCell<Jelo> cell = new ListCell<>() {


                @Override
                protected void updateItem(Jelo jelo, boolean empty) {
                    super.updateItem(jelo, empty);
                    if (empty || jelo == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        ImageView imageView = new ImageView(jelo.getImage());
                        imageView.setFitWidth(50);
                        imageView.setPreserveRatio(true);
                        setText("%s; %d din".formatted(jelo.getNaziv(), jelo.getCena()));
                        setGraphic(imageView);
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        jelaListView.getItems().setAll(restoran.getJela());
        nazivTextField.setText(restoran.getNaziv());
        adresaTextField.setText(restoran.getAdresa());
        if (restoran.getLogo() == null) {
            logoView.setImage(ImageUtils.textToImage("Nema\nslike", (int) logoView.getFitWidth(), (int) logoView.getFitHeight()));
        } else logoView.setImage(restoran.getLogo());

        ContextMenu logoContextMenu = new ContextMenu();
        MenuItem obrisiLogo = new MenuItem("Obrisi logo");
        obrisiLogo.addEventHandler(ActionEvent.ACTION, this::onDeleteLogo);
        logoContextMenu.getItems().add(obrisiLogo);

        logoView.setOnContextMenuRequested(contextMenuEvent ->
                logoContextMenu.show(logoView, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY()));

        ContextMenu jelaContextMenu = new ContextMenu();
        MenuItem dodajJelo = new MenuItem("Dodaj jelo");
        dodajJelo.addEventHandler(ActionEvent.ACTION, this::onDodajJelo);
        MenuItem obrisiJelo = new MenuItem("Obrisi jelo");
        obrisiJelo.addEventHandler(ActionEvent.ACTION, this::onObrisiJelo);
        obrisiJelo.setDisable(true);
        jelaContextMenu.getItems().add(dodajJelo);
        jelaContextMenu.getItems().add(obrisiJelo);
        jelaListView.setContextMenu(jelaContextMenu);
        jelaListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) ->
                obrisiJelo.setDisable(newValue == null));
    }

    public void onSacuvaj() {
        Map<String, Object> fields = new HashMap<>();
        String naziv = nazivTextField.getText();
        if (naziv.length() < 5) {
            nazivTextField.getStyleClass().add("error-field");
            errorLabel.setText("Naziv mora imati min 5 karaktera !");
            return;
        }
        fields.put("naziv", naziv);
        String adresa = adresaTextField.getText();
        if (adresa.length() < 5) {
            adresaTextField.getStyleClass().add("error-field");
            errorLabel.setText("Adresa mora imati makar 5 karaktera !");
            return;
        }
        fields.put("adresa", adresa);
        if (logoChanged) {
            if (newLogo == null) {
                fields.put("logo", null);
            } else {
                try {
                    fields.put("logo", new FileInputStream(newLogo));
                } catch (FileNotFoundException e) {
                    errorLabel.setText("Fajl izabranog logo-a nije pronadjen !");
                }
            }
        }

        storage.modifyRestoran(restoran, fields)
                .whenComplete(((unused, throwable) -> {
                    if (throwable != null) {
                        if (throwable.getCause() instanceof RestoranExistException) {
                            Platform.runLater(() -> errorLabel.setText("Restoran sa tim imenom vec postoji !"));
                        }
                    } else {
                        Platform.runLater(() -> successLabel.setText("Uspesno ste sacuvali promene !"));
                    }
                }));
    }

    public void onDeleteLogo(ActionEvent actionEvent) {
        newLogo = null;
        logoView.setImage(ImageUtils.textToImage("Nema\nslike", (int) logoView.getFitWidth(), (int) logoView.getFitHeight()));
        logoChanged = true;
    }

    public void onLogoClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() != MouseButton.PRIMARY) return;
        Window parentWindow = ((ImageView) mouseEvent.getSource()).getScene().getWindow();
        File file = ImagePicker.getInstance().open(parentWindow);
        if (file != null) {
            newLogo = file;
            logoView.setImage(new Image("file:///" + newLogo.getAbsolutePath()));
            logoChanged = true;
        }
    }

    public void onDodajJelo(ActionEvent actionEvent) {
        Scene scene = jelaListView.getScene();
        Parent parent = ViewLoader.load(TabbyViews.DODAJ_JELO, storage, scene, jelaListView, restoran);
        ((Stage) jelaListView.getScene().getWindow()).setScene(new Scene(parent));
    }

    public void onObrisiJelo(ActionEvent actionEvent) {
        Jelo jelo = jelaListView.getSelectionModel().getSelectedItem();
        Alert confirmation = new Alert(
                Alert.AlertType.WARNING,
                "Da li ste sigurni da zelite da obrisete jelo " + jelo.getNaziv() + "?",
                ButtonType.YES,
                ButtonType.NO
        );
        confirmation.setHeaderText(null);
        confirmation.setTitle("Upozorenje");
        confirmation.showAndWait();
        if (confirmation.getResult() == ButtonType.YES) {
            storage.obrisiJelo(jelo)
                    .thenRun(() -> Platform.runLater(() -> jelaListView.getItems().remove(jelo)));
        }
    }

    public void onFieldWrite(KeyEvent keyEvent) {
        TextField field = (TextField) keyEvent.getSource();
        field.getStyleClass().remove("error-field");
        errorLabel.setText(null);
        successLabel.setText(null);
    }
}
