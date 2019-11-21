package kosa.team5.gcs.service2;

import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kosa.team5.gcs.network.NetworkConfig;

public class ElectroMagnet {
    private static Logger logger = LoggerFactory.getLogger(ElectroMagnet.class);
    private MqttClient mqttClient;
    private String pubTopic;
    private String subTopic;
    public String status;


    public void mqttConnect(String mqttBrokerConnStr, String pubTopic, String subTopic){
        this.pubTopic = pubTopic;
        this.subTopic = subTopic;
            try{
                mqttClient = new MqttClient(mqttBrokerConnStr, MqttClient.generateClientId(),null);
                MqttConnectOptions option = new MqttConnectOptions();
                option.setConnectionTimeout(5);
                option.setAutomaticReconnect(true);
                mqttClient.connect(option);

                logger.info("Magnet service MQTT Connected: " + mqttBrokerConnStr);
            }catch(Exception e){
                e.printStackTrace();
        }
        mqttReceiveFromGcs();
    }
    private void mqttReceiveFromGcs() {
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                byte[] arr = mqttMessage.getPayload();
                String json = new String(arr);
                JSONObject obj = new JSONObject(json);
                System.out.println(obj);
                String action = obj.getString("status");
                System.out.println(action);
                if(action.equals("attach")){
                    status = action;
                    logger.info("attach");
                }else{
                    status = action;
                    logger.info("detach");
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            }
            @Override
            public void connectionLost(Throwable throwable) {
            }
        });

        try {
            mqttClient.subscribe(pubTopic);
            logger.info("Magnet service MQTT pubscribed: " + pubTopic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void magnetOn(){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action","on");
            String json = jsonObject.toString();
            mqttClient.publish(NetworkConfig.getInstance().droneTopic+"/magnet/sub",json.getBytes(),0,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void magnetOff(){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action","off");
            String json = jsonObject.toString();
            mqttClient.publish(NetworkConfig.getInstance().droneTopic+"/magnet/sub",json.getBytes(),0,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  public void sendOn() throws MqttException {
        mqttClient.publish(NetworkConfig.getInstance().droneTopic + "/magnet/sub", "on".getBytes(), 0, false);
    }
    public void sendOff() throws MqttException {
        mqttClient.publish(NetworkConfig.getInstance().droneTopic + "/magnet/sub", "off".getBytes(), 0, false);
    }


}
