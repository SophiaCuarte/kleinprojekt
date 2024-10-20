module ims {
    requires javafx.controls;
    requires javafx.fxml;

    opens ims to javafx.fxml;
    exports ims;
}
