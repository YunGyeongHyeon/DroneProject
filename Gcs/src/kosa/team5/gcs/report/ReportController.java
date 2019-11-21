package kosa.team5.gcs.report;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import kosa.team5.gcs.main.GcsMain;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URL;
import java.util.ResourceBundle;


public class ReportController implements Initializable {
    @FXML public Button btnOK;
    @FXML private VBox vbox;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnOK.setOnAction(btnOkEventHandler);
        initVBox();
    }

    public EventHandler<ActionEvent> btnOkEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Report.close();
        }
    };

    private void initVBox() {
        try {
            HBox hboxTitle = (HBox) FXMLLoader.load(Report.class.getResource("vbox_title.fxml"));
            vbox.getChildren().add(hboxTitle);
            for (ReportItem reportItem : Report.reports) {
                HBox hboxItem = (HBox) FXMLLoader.load(Report.class.getResource("vbox_item.fxml"));
                vbox.getChildren().add(hboxItem);

                Label lblNo = (Label) hboxItem.lookup("#lblNo");
                lblNo.setText(reportItem.getNo());
                Label lblLat = (Label) hboxItem.lookup("#lblLat");
                lblLat.setText(reportItem.getLat());
                Label lblLng = (Label) hboxItem.lookup("#lblLng");
                lblLng.setText(reportItem.getLon());
                Label lblTime = (Label) hboxItem.lookup("#lblTime");
                lblTime.setText(reportItem.getTime());
                Button btnMap = (Button) hboxItem.lookup("#btnMap");
                btnMap.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        GcsMain.instance.controller.flightMap.controller.setMissionItems(
                                Double.parseDouble(reportItem.getLat()),
                                Double.parseDouble((reportItem.getLon()))
                        );
                        MqttImageService mqttImageService = new MqttImageService();
                        mqttImageService.sendMessage2(reportItem.getNo());

                        Report.close();
                    }
                });

                Button btnComplete = (Button) hboxItem.lookup("#btnComplete");
                btnComplete.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //MQTT로 완료 처리 메시지 보낸다.
                        try {
                            Report.resultSend();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                        Report.reports.remove(reportItem);
                        reportUpdate();
                    }
                });
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void reportUpdate() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vbox.getChildren().clear();
                initVBox();
            }
        });
    }
}
