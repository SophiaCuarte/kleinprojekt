package ims;

import java.io.IOException;
import javafx.fxml.FXML;

public class logincontroller {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}