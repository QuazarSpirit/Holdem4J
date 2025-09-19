package org.quazarspirit.holdem4j;

import java.util.function.Consumer;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
import org.quazarspirit.Utils.PubSub.Event;
import org.quazarspirit.Utils.PubSub.ISubscriber;
import org.quazarspirit.Utils.PubSub.Publisher;

public class MqttService extends Publisher implements ISubscriber, MqttCallback {
    MemoryPersistence persistence = new MemoryPersistence();
    protected MqttClient _client;
    protected String _topic;
    protected Consumer<Event> onMessageCallback = null;

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

    public void publish(String topic, JSONObject jsonObject) {
        MqttMessage message = new MqttMessage(jsonObject.toString().getBytes());
        try {
            _client.publish(topic, message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void publish(JSONObject jsonObject) {
        super.publish(jsonObject);
        publish(_topic, jsonObject);
    }

    public void setOnMessage(Consumer<Event> onMessageCallback) {
        this.onMessageCallback = onMessageCallback;
    }

    @Override
    public void update(Event event) {
        if (this.onMessageCallback != null) {
            this.onMessageCallback.accept(event);
        }
    }

    @Override
    public void connectionLost(Throwable arg0) {
        System.err.println("Connection to broker lost");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {
        System.out.println("Connection to broker lost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        JSONObject object = new JSONObject(message.toString());

        Event event = new Event(this, object);
        update(event);
    }
}
