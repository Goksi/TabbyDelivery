package tech.goksi.projekatop.controllers.admin;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import tech.goksi.projekatop.PorudzbinaFilterChoice;
import tech.goksi.projekatop.models.Korisnik;
import tech.goksi.projekatop.models.NarucenoJelo;
import tech.goksi.projekatop.models.Porudzbina;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.utils.Injectable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class PorudzbineController implements Injectable {
    private final DataStorage storage;
    private final ObservableList<Porudzbina> porudzbine;
    private final FilteredList<Porudzbina> filteredPorudzbine;
    @FXML
    private TableView<Porudzbina> tableView;
    @FXML
    private TextField filterTextField;
    @FXML
    private ChoiceBox<PorudzbinaFilterChoice> filterChoiceBox;
    @FXML
    private TableColumn<Porudzbina, Integer> idColumn;
    @FXML
    private TableColumn<Porudzbina, Korisnik> korisnikColumn;
    @FXML
    private TableColumn<Porudzbina, Date> datumColumn;
    @FXML
    private TableColumn<Porudzbina, Set<NarucenoJelo>> jelaColumn;

    public PorudzbineController(DataStorage storage) {
        this.storage = storage;
        this.porudzbine = FXCollections.observableArrayList();
        this.filteredPorudzbine = new FilteredList<>(porudzbine);
    }

    public void initialize() {
        filterChoiceBox.setValue(PorudzbinaFilterChoice.ID);
        filterTextField.textProperty().addListener((obs, oldValue, newValue) -> onTextChange(newValue));
        idColumn.setCellValueFactory(porudzbinaCellData -> new ReadOnlyObjectWrapper<>(porudzbinaCellData.getValue().getId()));
        korisnikColumn.setCellValueFactory(porudzbinaCellData -> new ReadOnlyObjectWrapper<>(porudzbinaCellData.getValue().getKorisnik()));
        datumColumn.setCellValueFactory(porudzbinaCellData -> new ReadOnlyObjectWrapper<>(porudzbinaCellData.getValue().getVremePorudzbine()));
        jelaColumn.setCellValueFactory(porudzbinaCellData -> new ReadOnlyObjectWrapper<>(porudzbinaCellData.getValue().getNarucenoJela()));
        idColumn.setCellFactory(kolona -> {
            TableCell<Porudzbina, Integer> tableCell = new TableCell<>() {
                @Override
                protected void updateItem(Integer integer, boolean empty) {
                    super.updateItem(integer, empty);
                    if (empty || integer == null) {
                        setText(null);
                    } else {
                        setText(integer.toString());
                    }
                }
            };
            tableCell.setAlignment(Pos.CENTER);
            return tableCell;
        });

        korisnikColumn.setCellFactory(kolona -> {
            TableCell<Porudzbina, Korisnik> tableCell = new TableCell<>() {
                @Override
                protected void updateItem(Korisnik korisnik, boolean empty) {
                    super.updateItem(korisnik, empty);
                    if (empty || korisnik == null) {
                        setText(null);
                    } else {
                        setText(korisnik.getUsername());
                    }
                }
            };
            tableCell.setAlignment(Pos.CENTER);
            return tableCell;
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        datumColumn.setCellFactory(kolona -> {
            TableCell<Porudzbina, Date> tableCell = new TableCell<>() {
                @Override
                protected void updateItem(Date date, boolean empty) {
                    super.updateItem(date, empty);
                    if (empty || date == null) {
                        setText(null);
                    } else {
                        setText(dateFormat.format(date));
                    }
                }
            };
            tableCell.setAlignment(Pos.CENTER);
            return tableCell;
        });

        jelaColumn.setCellFactory(kolona -> {
            TableCell<Porudzbina, Set<NarucenoJelo>> tableCell = new TableCell<>() {
                @Override
                protected void updateItem(Set<NarucenoJelo> narucenaJela, boolean empty) {
                    super.updateItem(narucenaJela, empty);
                    if (empty || narucenaJela == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        ScrollPane scrollPane = new ScrollPane();
                        scrollPane.setMaxHeight(50);
                        VBox root = new VBox(5);
                        List<Label> labele = narucenaJela.stream().map(jelo -> new Label(jelo.toString())).toList();
                        root.getChildren().addAll(labele);
                        scrollPane.setContent(root);
                        setGraphic(scrollPane);
                    }
                }
            };
            tableCell.setAlignment(Pos.CENTER);
            return tableCell;
        });
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Obrisi porudzbinu");
        menuItem.addEventHandler(ActionEvent.ACTION, this::onObrisiPorudzbinu);
        contextMenu.getItems().add(menuItem);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                tableView.setContextMenu(null);
            } else {
                tableView.setContextMenu(contextMenu);
            }
        });
        tableView.setItems(filteredPorudzbine);
        populateTableView();
    }

    private void onObrisiPorudzbinu(ActionEvent actionEvent) {
        Porudzbina porudzbina = tableView.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.WARNING, "Da li ste sigurni da zelite da obrisete porudzbinu sa id-om " + porudzbina.getId() + "?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        alert.setTitle("Upozorenje");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.NO) return;
        storage.obrisiPorudzbinu(porudzbina)
                .thenRun(() -> {
                    Platform.runLater(() -> {
                        Alert info = new Alert(Alert.AlertType.INFORMATION, "Uspesno obrisana porudzbina !", ButtonType.OK);
                        alert.setHeaderText(null);
                        alert.setTitle("Uspesno");
                        info.show();
                        populateTableView();
                    });
                });
    }


    public void onTextChange(String newValue) {
        PorudzbinaFilterChoice choice = filterChoiceBox.getValue();
        filter(newValue, choice);
    }

    public void onChoiceChange() {
        PorudzbinaFilterChoice choice = filterChoiceBox.getValue();
        String value = filterTextField.getText();
        filter(value, choice);
    }

    private void filter(String prompt, PorudzbinaFilterChoice filterChoice) {
        if (prompt.isEmpty()) {
            filteredPorudzbine.setPredicate(p -> true);
            return;
        }
        if (filterChoice == PorudzbinaFilterChoice.ID) {
            filteredPorudzbine.setPredicate(p -> Integer.toString(p.getId()).contains(prompt));
        } else if (filterChoice == PorudzbinaFilterChoice.KORISNIK) {
            filteredPorudzbine.setPredicate(p -> p.getKorisnik().getUsername().toLowerCase().contains(prompt.toLowerCase()));
        }
    }

    private void populateTableView() {
        storage.getAllPorudzbine()
                .thenAccept(nove -> Platform.runLater(() -> porudzbine.setAll(nove)));
    }

}

