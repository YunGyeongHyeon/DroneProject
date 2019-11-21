package kosa.team5.gcs.missionEnd;

import kosa.team5.gcs.main.GcsMain;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import syk.common.MavJsonMessage;
import syk.drone.mavlink.Messages.MAVLinkMessage;

public class DroneMissionComplete {
    private static final Logger log = LoggerFactory.getLogger(DroneMissionComplete.class);
    //==================================================================================
    public DroneMissionComplete() {
        log.info("Drone Mission Complete object creating now");
    }
    //==================================================================================
    public void droneMissionComplete() {
        log.info("droneMissionComplete() start");
        JSONArray usedWayPoint = GcsMain.instance.controller.flightMap.controller.getMissionItems();
        System.out.println(usedWayPoint);
        JSONArray wayBackHome = new JSONArray();
        double x = usedWayPoint.getJSONObject(0).getDouble("x");
        double y = usedWayPoint.getJSONObject(0).getDouble("y");
        wayBackHome.put(usedWayPoint.getJSONObject(0));
        for (int i=usedWayPoint.length()-2; i>0; i--) {
            //경로 뒤집기
            int newSeq = usedWayPoint.length() - usedWayPoint.getJSONObject(i).getInt("seq")-1;
            JSONObject obj = usedWayPoint.getJSONObject(i);
            obj.put("seq", newSeq);
            wayBackHome.put(obj);
            if (i == 1) {
                //wayBackHome의 해당 인덱스 RTL로 만들기
                obj.put("seq", newSeq);
                obj.put("command", MavJsonMessage.MAVJSON_MISSION_COMMAND_RTL);
                obj.put("param1", "0");
                obj.put("param2", "0");
                obj.put("param3", "0");
                obj.put("param4", "0");
                obj.put("x", x);
                obj.put("y", y);
                obj.put("z", 0);
            }
        }
        System.out.println(wayBackHome);
        GcsMain.instance.controller.flightMap.controller.missionClear();
        GcsMain.instance.controller.flightMap.controller.setMissionItems2(wayBackHome);
        GcsMain.instance.controller.drone.flightController.sendMissionUpload(wayBackHome);
    }
    //==================================================================================
}
