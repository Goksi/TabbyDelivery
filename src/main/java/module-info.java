module tech.goksi.projekatop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens tech.goksi.projekatop to javafx.fxml;
    opens tech.goksi.projekatop.controllers to javafx.fxml;
    exports tech.goksi.projekatop;
}