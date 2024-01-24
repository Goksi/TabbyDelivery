package tech.goksi.projekatop.controllers.admin;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;
import tech.goksi.projekatop.models.Jelo;
import tech.goksi.projekatop.models.Restoran;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.persistance.DataStorageInjectable;
import tech.goksi.projekatop.utils.ImagePicker;
import tech.goksi.projekatop.utils.ImageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class DodajJeloController implements DataStorageInjectable {
    private final SimpleObjectProperty<File> imageProperty;
    @FXML
    private TextField nazivField;
    @FXML
    private TextField cenaField;
    @FXML
    private ImageView imageView;
    @FXML
    private Label successLabel;
    @FXML
    private Label errorLabel;
    private DataStorage storage;
    private Scene urediScene;
    private ListView<Jelo> jelaListView;

    public DodajJeloController() {
        imageProperty = new SimpleObjectProperty<>();
    }

    @SuppressWarnings("unchecked") // this will always work
    public void initialize() {
        Image noImage = ImageUtils.textToImage("Nema\nslike", (int) imageView.getFitWidth(), (int) imageView.getFitHeight());
        imageView.setImage(noImage);
        imageView.sceneProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) return;
            Pair<Scene, ListView<Jelo>> userData = (Pair<Scene, ListView<Jelo>>) newValue.getRoot().getUserData();
            urediScene = userData.getKey();
            jelaListView = userData.getValue();
        });
        ContextMenu contextMenu = new ContextMenu();
        MenuItem obrisiSliku = new MenuItem("Obrisi sliku");
        obrisiSliku.setDisable(true);
        obrisiSliku.addEventHandler(ActionEvent.ACTION, this::onSlikaDelete);
        contextMenu.getItems().add(obrisiSliku);
        imageView.setOnContextMenuRequested(contextMenuEvent ->
                contextMenu.show(imageView, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY()));
        imageProperty.addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                imageView.setImage(noImage);
                obrisiSliku.setDisable(true);
            } else {
                imageView.setImage(new Image("file:///" + newValue.getAbsolutePath()));
                obrisiSliku.setDisable(false);
            }
        });

    }

    @Override
    public void setDataStorage(DataStorage storage) {
        this.storage = storage;
    }

    public void onSlikaClick(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) return;
        Window parent = imageView.getScene().getWindow();
        File file = ImagePicker.getInstance().open(parent);
        if (file == null) return;
        imageProperty.set(file);
    }

    public void onSlikaDelete(ActionEvent event) {
        imageProperty.set(null);
    }

    public void onDodajJelo() {
        String naziv = nazivField.getText();
        if (naziv.isEmpty()) {
            errorLabel.setText("Naziv ne sme da bude prazan !");
            nazivField.getStyleClass().add("error-field");
            return;
        }
        String cenaRaw = cenaField.getText();
        int cena;
        try {
            cena = Integer.parseInt(cenaRaw);
        } catch (NumberFormatException e) {
            errorLabel.setText("Cena mora biti broj !");
            cenaField.getStyleClass().add("error-field");
            return;
        }
        if (cena < 0) {
            errorLabel.setText("Cena ne sme biti negativna !");
            cenaField.getStyleClass().add("error-field");
            return;
        }
        InputStream imageStream = null;
        if (imageProperty.isNotNull().get()) {
            try {
                imageStream = new FileInputStream(imageProperty.get());
            } catch (FileNotFoundException e) {
                errorLabel.setText("Slika nije pronadjena !");
                return;
            }
        }
        Restoran restoran = (Restoran) urediScene.getUserData();
        storage.addJeloToRestoran(restoran, naziv, imageStream, cena)
                .thenAccept(jelo -> Platform.runLater(() -> {
                    successLabel.setText("Uspesno ste dodali jelo u restoran " + restoran.getNaziv() + "!");
                    imageProperty.set(null);
                    nazivField.setText(null);
                    cenaField.setText(null);
                    jelaListView.getItems().add(jelo);
                }));
    }

    public void onBack(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        ((Stage) button.getScene().getWindow()).setScene(urediScene);
    }

    public void onFieldWrite(KeyEvent keyEvent) {
        TextField textField = (TextField) keyEvent.getSource();
        textField.getStyleClass().remove("error-field");
        errorLabel.setText(null);
        successLabel.setText(null);
    }
}
