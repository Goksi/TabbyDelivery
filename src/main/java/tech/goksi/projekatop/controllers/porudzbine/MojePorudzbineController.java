package tech.goksi.projekatop.controllers.porudzbine;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import tech.goksi.projekatop.models.Korisnik;
import tech.goksi.projekatop.models.NarucenoJelo;
import tech.goksi.projekatop.models.Porudzbina;
import tech.goksi.projekatop.paginating.PageNavigator;
import tech.goksi.projekatop.persistance.DataStorage;
import tech.goksi.projekatop.utils.Injectable;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class MojePorudzbineController implements Injectable {
    private final ObservableList<Porudzbina> porudzbine;
    private final DataStorage storage;
    private final Korisnik trenutniKorisnik;
    private final PageNavigator pageNavigator;
    @FXML
    private TableColumn<Porudzbina, Date> datumColumn;
    @FXML
    private TableColumn<Porudzbina, Set<NarucenoJelo>> porudzbinaColumn;
    @FXML
    private TableView<Porudzbina> tableView;
    @FXML
    private Label infoLabel;
    private String pattern;

    public MojePorudzbineController(DataStorage storage, Korisnik trenutniKorisnik, PageNavigator pageNavigator) {
        porudzbine = FXCollections.observableArrayList();
        this.storage = storage;
        this.trenutniKorisnik = trenutniKorisnik;
        this.pageNavigator = pageNavigator;
    }

    public void initialize() {
        pattern = infoLabel.getText();
        infoLabel.setText(null);
        datumColumn.setCellValueFactory(porudzbinaDateCellDataFeatures -> new ReadOnlyObjectWrapper<>(porudzbinaDateCellDataFeatures.getValue().getVremePorudzbine()));
        porudzbinaColumn.setCellValueFactory(porudzbinaSetCellDataFeatures -> new ReadOnlyObjectWrapper<>(porudzbinaSetCellDataFeatures.getValue().getNarucenoJela()));
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
        porudzbinaColumn.setCellFactory(kolona -> {
            TableCell<Porudzbina, Set<NarucenoJelo>> tableCell = new TableCell<>() {
                @Override
                protected void updateItem(Set<NarucenoJelo> narucenaJela, boolean empty) {
                    super.updateItem(narucenaJela, empty);
                    if (empty || narucenaJela == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        ScrollPane scrollPane = new ScrollPane();
                        scrollPane.setMaxHeight(60);
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
        MenuItem otkaziPorudzbinu = new MenuItem("Otkazi porudzbinu");
        otkaziPorudzbinu.addEventHandler(ActionEvent.ACTION, this::onOtkaziPorudzbinu);
        contextMenu.getItems().add(otkaziPorudzbinu);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                tableView.setContextMenu(null);
                infoLabel.setText(null);
            } else {
                tableView.setContextMenu(contextMenu);
                onItemChange(newValue);
            }
        });

        tableView.setItems(porudzbine);
        populateTableView();
    }

    public void onNovaPorudzbina() {
        pageNavigator.goToPage(PageNavigator.Page.NOVA_PORUDZBINA);
    }

    public void onItemChange(Porudzbina porudzbina) {
        infoLabel.setText(pattern.formatted(porudzbina.getCena(), getDurationText(porudzbina)));
    }

    public void onOtkaziPorudzbinu(ActionEvent actionEvent) {
        Porudzbina porudzbina = tableView.getSelectionModel().getSelectedItem();
        if (!canCancel(porudzbina)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Nije moguce otkazati porudzbinu, proslo je vise od 15 minuta !", ButtonType.OK);
            alert.setHeaderText(null);
            alert.setTitle("Upozorenje");
            alert.show();
            return;
        }
        storage.obrisiPorudzbinu(porudzbina)
                .thenRun(() -> {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Uspesno ste otkazali porudzbinu !", ButtonType.OK);
                        alert.setHeaderText(null);
                        alert.setTitle("Info");
                        alert.show();
                        populateTableView();
                    });
                });
    }

    private void populateTableView() {
        storage.getAllPorudzbine(trenutniKorisnik)
                .thenAccept(nove -> Platform.runLater(() -> porudzbine.setAll(nove)));
    }

    private boolean canCancel(Porudzbina porudzbina) {
        long a = getDuration(porudzbina).toSeconds();
        return getDuration(porudzbina).toSeconds() < (15 * 60);
    }

    public String getDurationText(Porudzbina porudzbina) {
        Duration duration = getDuration(porudzbina);
        long minutes = duration.toMinutes();

        if (minutes >= 15) {
            return "Nije moguce";
        } else {
            return 15 - minutes + " minuta";
        }
    }

    private Duration getDuration(Porudzbina porudzbina) {
        Instant instant = Instant.ofEpochMilli(porudzbina.getVremePorudzbine().getTime());
        Instant now = Instant.now();
        return Duration.between(instant, now);
    }
}
