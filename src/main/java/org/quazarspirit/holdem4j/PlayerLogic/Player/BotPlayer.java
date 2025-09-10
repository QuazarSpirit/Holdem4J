package org.quazarspirit.holdem4j.PlayerLogic.Player;

import org.json.JSONArray;
import org.json.JSONObject;
import org.quazarspirit.Utils.PubSub.Event;
import org.quazarspirit.holdem4j.GameLogic.Game;
import org.quazarspirit.holdem4j.PlayerLogic.PlayerActionEnum;
import org.quazarspirit.holdem4j.PlayerLogic.PlayerIntentEnum;
import org.quazarspirit.holdem4j.RoomLogic.DealerIntentEnum;
import org.quazarspirit.holdem4j.RoomLogic.Table;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Bot implementation of Player
 * Stays exclusively on server in same process
 */
public class BotPlayer extends Player {
    /**
     * Create Bot player with Random UUID and defined username
     * 
     * @param username String shown in client
     */
    public BotPlayer(String username) {
        super(UUID.randomUUID(), username);
    }

    public BotPlayer(UUID uuid, String username) {
        super(uuid, username);
    }

    @Override
    public void update(Event event) {
        JSONObject eventData = event.data;
        if (eventData.get("type") == DealerIntentEnum.QUERY_ACTION
                && eventData.get("player") == this) {
            handleQueryAction(eventData);
        }
    }

    void handleQueryAction(JSONObject eventData) {
        JSONArray jsonArray = (JSONArray) eventData.get("allowed_actions");
        ArrayList<PlayerActionEnum> allowedActions = new ArrayList<PlayerActionEnum>();

        for (Object obj : jsonArray) {
            PlayerActionEnum playerAction = (PlayerActionEnum) obj;
            allowedActions.add(playerAction);
        }

        Table table = (Table) eventData.get("table");
        Game game = table.getGame();

        PlayerActionEnum key;
        if (allowedActions.contains(PlayerActionEnum.CHECK)) {
            key = PlayerActionEnum.CHECK;
        } else {
            key = PlayerActionEnum.FOLD;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", PlayerIntentEnum.ACT);
    }

}
