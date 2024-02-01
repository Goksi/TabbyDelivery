package tech.goksi.projekatop.controllers.porudzbine;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import tech.goksi.projekatop.models.Jelo;
import tech.goksi.projekatop.utils.ImageUtils;
import tech.goksi.projekatop.utils.Injectable;
import tech.goksi.projekatop.utils.PorudzbinaMaker;

import java.util.Objects;

public class JeloKarticaController implements Injectable {
    private final Jelo jelo;
    private final PorudzbinaMaker trenutnaPorudzbina;
    @FXML
    private ImageView imageView;
    @FXML
    private Label infoLabel;

    public JeloKarticaController(Jelo jelo, PorudzbinaMaker trenutnaPorudzbina) {
        this.jelo = jelo;
        this.trenutnaPorudzbina = trenutnaPorudzbina;
    }

    public void initialize() {
        imageView.setImage(Objects.requireNonNullElseGet(jelo.getImage(), () -> ImageUtils.textToImage("Nema\nslike", (int) imageView.getFitWidth(), (int) imageView.getFitHeight())));
        ImageUtils.centerImage(imageView);
        infoLabel.setText(infoLabel.getText().formatted(jelo.getNaziv(), jelo.getCena()));
    }


    public void onClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() % 2 == 0) {
            trenutnaPorudzbina.dodajJelo(jelo);
        }
    }
}
