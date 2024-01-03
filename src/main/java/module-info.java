module tech.goksi.projekatop {
    requires javafx.controls;
    requires javafx.fxml;


    opens tech.goksi.projekatop to javafx.fxml;
    exports tech.goksi.projekatop;
}