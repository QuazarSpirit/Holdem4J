package org.quazarspirit.holdem4j.player_logic.player;

import org.json.JSONObject;
import org.quazarspirit.holdem4j.room_logic.Table;
import org.quazarspirit.utils.publisher_subscriber_pattern.IPublisher;
import org.quazarspirit.utils.publisher_subscriber_pattern.ISubscriber;

import java.util.UUID;

public interface IPlayer extends ISubscriber, IPublisher {
    UUID getUUID();
    String getUsername();
    void addTable(Table table);
    void publish(JSONObject jsonObject);
    boolean equals(IPlayer playerToCheck);
    void addSubscriber(ISubscriber subscriber);
    void removeSubscriber(ISubscriber subscriber);
}
