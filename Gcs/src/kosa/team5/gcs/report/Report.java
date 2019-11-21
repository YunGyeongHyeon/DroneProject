package kosa.team5.gcs.report;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import kosa.team5.gcs.main.GcsMain;
import kosa.team5.gcs.main.GcsMainController;
import kosa.team5.gcs.missionEnd.DroneMissionComplete;
import kosa.team5.gcs.network.Drone;
import kosa.team5.gcs.network.NetworkConfig;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Report {
    static MqttClient mqttClient;
    String pubTopic;
    String subTopic;
    public static List<ReportItem> reports = new ArrayList<ReportItem>();
    private static Logger logger = LoggerFactory.getLogger(Report.class);
    private static Stage stage;
    private static ReportController controller;
    private static GcsMainController gcsMainController;

    static DroneMissionComplete dmc;
    static Drone drone;

    public Report() {
    }

    public void mqttConnect(String mqttBrokerConnStr, String pubTopic, String subTopic){
        this.pubTopic = pubTopic;
        this.subTopic = subTopic;

            try{
                mqttClient = new MqttClient(mqttBrokerConnStr, MqttClient.generateClientId(),null);
                MqttConnectOptions option = new MqttConnectOptions();
                option.setConnectionTimeout(5);
                option.setAutomaticReconnect(true);
                mqttClient.connect(option);
                dmc = new DroneMissionComplete();

                logger.info("Report MQTT Connected: " + mqttBrokerConnStr);

            }catch(Exception e){
                e.printStackTrace();
                try { mqttClient.close(); } catch (Exception e1) {}
                try { Thread.sleep(1000); } catch (InterruptedException e1) {}
            }
        mqttReceiveFromWeb();
    }

    private void mqttReceiveFromWeb() {
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                byte[] arr = mqttMessage.getPayload();
                String json = new String(arr);
                JSONObject obj = new JSONObject(json);
                ReportItem reportItem = new ReportItem();
                reportItem.setLat(obj.getString("lat"));
                reportItem.setLon(obj.getString("lon"));
                reportItem.setNo(obj.getString("report_no"));
                reportItem.setTime(obj.getString("report_time"));
                reports.add(0, reportItem);
                GcsMain.instance.controller.flightMap.controller.showInfoLabel("새로운 출동요청이 접수되었습니다.");
                dialogRefresh();
                sendMessage();
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            }
            @Override
            public void connectionLost(Throwable throwable) {
            }
        });

        try {
            mqttClient.subscribe(subTopic);
            logger.info("Magnet MQTT subscribed: " + subTopic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    mqttClient.publish(NetworkConfig.getInstance().droneTopic + "/report/return", "ok".getBytes(), 0, false);
                }catch (Exception e){}
            }
        };;
    }

    public static void show(){
        try{
            stage = new Stage(StageStyle.UTILITY);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(GcsMain.instance.primaryStage);

            FXMLLoader fxmlLoader = new FXMLLoader(Report.class.getResource("Report.fxml"));
            BorderPane pane = fxmlLoader.load();
            controller = fxmlLoader.getController();
            FXMLLoader fxmlGcsLoader = new FXMLLoader(Report.class.getResource("GcsMain.fxml"));
            gcsMainController = fxmlGcsLoader.getController();

            Scene scene = new Scene(pane);
            scene.getStylesheets().add(GcsMain.class.getResource("style_dark.css").toExternalForm());

            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e){e.printStackTrace();}
    }

    public static void close(){
        stage.close();
    }

    public void dialogRefresh() {
        if(controller == null) return;
        controller.reportUpdate();
    }

    public static void resultSend() throws MqttException {
        JSONArray jsonArray = GcsMain.instance.controller.flightMap.controller.getMissionItems();

        //======================================================================================
        dmc.droneMissionComplete();
        GcsMain.instance.controller.drone.flightController.sendMissionStart();
        String byteJson = jsonArray.toString();
        byte[] json = byteJson.getBytes();

        System.out.println("resultSend() 실행 : "+jsonArray);
        mqttClient.publish("/drone/path/pub",json, 0, false);
}

    public void accidentSend(String result) throws MqttException {
        byte[] json = result.getBytes();
        System.out.println("accidentSend() 실행 : " + json);
        mqttClient.publish("/drone/path/accident", json, 0, false);
        mqttClient.publish("/drone/service2/accident", json, 0, false);

    }
}
