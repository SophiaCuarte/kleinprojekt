module ims {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.codec;
    requires java.sql;
    requires jbcrypt;

    opens ims to javafx.fxml;
    exports ims;
}