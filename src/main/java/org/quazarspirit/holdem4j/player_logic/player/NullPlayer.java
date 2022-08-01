package org.quazarspirit.holdem4j.player_logic.player;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;
import org.quazarspirit.holdem4j.room_logic.Table;
import org.quazarspirit.utils.publisher_subscriber_pattern.Event;
import org.quazarspirit.utils.publisher_subscriber_pattern.IPublisher;
import org.quazarspirit.utils.publisher_subscriber_pattern.ISubscriber;
import org.quazarspirit.utils.publisher_subscriber_pattern.Publisher;

import java.util.UUID;

public class NullPlayer implements IPlayer {
    static final private NullPlayer _singleton = new NullPlayer();
    static final String NULL_USERNAME = "NULL_USERNAME";
    static final private UUID _uuid =  UUID.fromString("00000000-0000-1000-2000-000000000000");
    private final IPublisher _publisher = new Publisher(this);
    private NullPlayer() {}
    static public NullPlayer GetSingleton() {
        return _singleton;
    }
    @Override
    public String getUsername() {
        return NullPlayer.NULL_USERNAME;
    }
    @Override
    public void addTable(Table table) {}

    /**
     * @param playerToCheck IPlayer instance to check
     * @return true if instance of NullPlayer (singleton) otherwise false
     */
    @Override
    public boolean equals(IPlayer playerToCheck) {
        return this == playerToCheck;
    }

    @Override
    public void addSubscriber(ISubscriber subscriber) {
        _publisher.addSubscriber(subscriber);
    }

    @Override
    public void removeSubscriber(ISubscriber subscriber) {
        _publisher.removeSubscriber(subscriber);
    }

    @Override
    public void publish(JSONObject jsonObject) {
        _publisher.publish(jsonObject);
    }

    /**
     * @return Null uuid
     */
    @Override
    public UUID getUUID() {
        return _uuid;
    }

    /**
     * @param event Event
     */
    @Override
    public void update(Event event) {

    }
}
