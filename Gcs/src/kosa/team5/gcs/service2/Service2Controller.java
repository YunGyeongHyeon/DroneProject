package kosa.team5.gcs.service2;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import kosa.team5.gcs.network.NetworkConfig;
import java.net.URL;
import java.util.ResourceBundle;

public class Service2Controller implements Initializable {
    @FXML private Button btnCancel;
    @FXML private Button magnetOn;
    @FXML private Button magnetOff;
    @FXML private Label changeText;

    ElectroMagnet magnet = new ElectroMagnet();
    NetworkConfig networkConfig = new NetworkConfig();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        magnet.mqttConnect(
                networkConfig.mqttBrokerConnStr,
                networkConfig.droneTopic +"/magnet/pub",
                networkConfig.droneTopic +"/magnet/sub"
        );
        btnCancel.setOnAction(btnCancelEventHandler);
        magnetOn.setOnAction(magnetOnEventHandler);
        magnetOff.setOnAction(magnetOffEventHandler);
    }


    private EventHandler<ActionEvent> btnCancelEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        }
    };
    private EventHandler<ActionEvent> magnetOnEventHandler = new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent event) {
            magnet.magnetOn();
            while (true){
                if(!changeText.getText().equals(magnet.status)){
                    changeText.setText(magnet.status);
                    break;
                }
            }
        }
    };
    private EventHandler<ActionEvent> magnetOffEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            magnet.magnetOff();
            while (true){
                if(!changeText.getText().equals(magnet.status)){
                    changeText.setText(magnet.status);
                    break;
                }
            }
        }
    };
}
