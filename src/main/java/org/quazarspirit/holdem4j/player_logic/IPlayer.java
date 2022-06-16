package org.quazarspirit.holdem4j.player_logic;
import org.json.JSONObject;
import org.quazarspirit.holdem4j.game_logic.Bet;
import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.holdem4j.room_logic.Table;
import org.quazarspirit.utils.KV;
import org.quazarspirit.utils.publisher_subscriber_pattern.IPublisher;
import org.quazarspirit.utils.publisher_subscriber_pattern.ISubscriber;

import java.util.ArrayList;
import java.util.UUID;

public interface IPlayer extends ISubscriber, IPublisher {
    UUID getUUID();
    String getUsername();
    KV<PLAYER_ACTION, Bet> queryAction(ArrayList<PLAYER_ACTION> allowedActions, Game gameArg);

    void addTable(Table table);
    boolean equals(IPlayer playerToCheck);

    void addSubscriber(ISubscriber subscriber);
    void removeSubscriber(ISubscriber subscriber);
    void publish(JSONObject jsonObject);
}
