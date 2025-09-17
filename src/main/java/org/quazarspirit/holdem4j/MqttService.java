package org.quazarspirit.holdem4j;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
import org.quazarspirit.Utils.PubSub.Publisher;

public class MqttService extends Publisher {
    MemoryPersistence persistence = new MemoryPersistence();
    protected MqttClient _client;
    protected String _topic;

    public MqttService(String broker, String clientId, String topic) {
        try {
            _client = new MqttClient(broker, clientId, persistence);
            _client.connect();
            _topic = topic;
        } catch (MqttException e) {
            System.err.println("MqttService is unable to connect");
            e.printStackTrace();
            System.exit(-4);
        }
    }

    @Override
    public void publish(JSONObject jsonObject) {

        MqttMessage message = new MqttMessage(jsonObject.toString().getBytes());
        try {
            _client.publish(_topic, message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
