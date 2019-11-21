package kosa.team5.drone.main;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.pi4j.io.gpio.*;
import kosa.team5.drone.network.NetworkConfig;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElectroMagnet {
    private final static String ATTACH = "attach";
    private final static String DETACH = "detach";

    private GpioPinDigitalOutput pin1;
    private GpioPinDigitalOutput pin2;

    private String status = "detach";

    private static Logger logger = LoggerFactory.getLogger(ElectroMagnet.class);
    private MqttClient mqttClient;
    private String pubTopic;
    private String subTopic;


    public ElectroMagnet(){
        GpioController gpioController = GpioFactory.getInstance();
        pin1 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_24, PinState.LOW);
        pin1.setShutdownOptions(true, PinState.LOW);
        pin2 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_25, PinState.LOW);
        pin2.setShutdownOptions(true, PinState.LOW);
    }


    public void mqttConnect(String mqttBrokerConnStr, String pubTopic, String subTopic){
        this.pubTopic = pubTopic;
        this.subTopic = subTopic;
        while(true){
            try{
                mqttClient = new MqttClient(mqttBrokerConnStr, MqttClient.generateClientId(),null);
                MqttConnectOptions option = new MqttConnectOptions();
                option.setConnectionTimeout(5);
                option.setAutomaticReconnect(true);
                mqttClient.connect(option);

                logger.info("Magnet MQTT Connected: " + mqttBrokerConnStr);
                break;
            }catch(Exception e){
                e.printStackTrace();
                try { mqttClient.close(); } catch (Exception e1) {}
                try { Thread.sleep(1000); } catch (InterruptedException e1) {}
            }
        }
        mqttReceiveFromGcs();
        //thread.start();
    }
    private void mqttReceiveFromGcs() {
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                byte[] arr = mqttMessage.getPayload();
                String json = new String(arr);
                JSONObject obj = new JSONObject(json);
                String action = obj.getString("action");
                if(action.equals("on")){
                    logger.info("on");
                    status = ATTACH;
                    attach();
                }else{
                    logger.info("off");
                    status = DETACH;
                    detach();
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
            mqttClient.subscribe(subTopic);
            logger.info("Magnet MQTT subscribed: " + subTopic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
    Thread thread = new Thread(){
        @Override
        public void run() {
            while (true) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("status", status);
                System.out.println(status + "??");
                String json = jsonObject.toString();
                try {
                    mqttClient.publish(NetworkConfig.getInstance().droneTopic + "/magnet/pub", json.getBytes(), 0, false);
                    thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };*/


    public void attach(){
        pin1.high();
        pin2.high();
        this.status = ATTACH;
    }

    public void detach(){
        pin1.low();
        pin2.low();
        this.status = DETACH;
    }

    public String getStatus(){
        return status;
    }
    public void sendOn() throws MqttException {
        mqttClient.publish(NetworkConfig.getInstance().droneTopic + "/magnet/sub", "on".getBytes(), 0, false);
    }
    public void sendOff() throws MqttException {
        mqttClient.publish(NetworkConfig.getInstance().droneTopic + "/magnet/sub", "off".getBytes(), 0, false);
    }


}
