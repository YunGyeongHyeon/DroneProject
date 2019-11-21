package kosa.team5.gcs.service1;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import kosa.team5.gcs.main.GcsMain;

import java.net.URL;
import java.util.ResourceBundle;

public class Service1Controller implements Initializable {
    @FXML private Button btnOk;
    @FXML private Button btnCancel;
    @FXML private TextField txtLat;
    @FXML private TextField txtLng;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnOk.setOnAction(btnOkEventHandler);
        btnCancel.setOnAction(btnCancelEventHandler);
    }

    private EventHandler<ActionEvent> btnOkEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            double lat = Double.parseDouble(txtLat.getText());
            double lng = Double.parseDouble(txtLng.getText());
            GcsMain.instance.controller.flightMap.controller.setMissionItems(lat,lng);
            Stage stage = (Stage)btnCancel.getScene().getWindow();
            stage.close();
        }
    };

    private EventHandler<ActionEvent> btnCancelEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        }
    };

}
