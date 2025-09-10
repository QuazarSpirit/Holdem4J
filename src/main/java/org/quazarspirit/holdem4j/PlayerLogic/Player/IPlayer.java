package org.quazarspirit.holdem4j.PlayerLogic.Player;

import org.json.JSONObject;
import org.quazarspirit.Utils.PubSub.IPublisher;
import org.quazarspirit.Utils.PubSub.ISubscriber;
import org.quazarspirit.holdem4j.RoomLogic.Table;

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
