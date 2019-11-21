package kosa.team5.gcs.report;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttImageService {

	
    private MqttClient client;
    private byte[] images;
    public ExecutorService threadPool = Executors.newFixedThreadPool(1);
    //--------------------------------------------
    
    public MqttImageService(){
        mqttConn();
    }

    public void mqttConn(){
        try {
            client = new MqttClient("tcp://106.253.56.124:1885", MqttClient.generateClientId(), null);
            client.connect();
            receiveMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mqttDisconn() {
        try {
            client.disconnect();
            client.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage() throws Exception {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                images = mqttMessage.getPayload();
                if(images != null) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                client.publish("/web/drone/cam0/pub", images, 0, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    threadPool.submit(runnable);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        client.subscribe("/drone/cam0/pub");
    }

    public void sendMessage(String message) {
        try {
            client.publish("/drone/cam0/gcs", message.getBytes(), 0, false);
        } catch(Exception e) {}
    }

    public void sendMessage2(String message) {
        try {
            client.publish("/drone/cam0/rno", message.getBytes(), 0, false);
            client.publish("/drone/path/reportno", message.getBytes(), 0, false);
        } catch(Exception e) {}
    }
}
