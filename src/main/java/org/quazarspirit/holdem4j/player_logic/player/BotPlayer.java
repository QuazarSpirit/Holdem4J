package org.quazarspirit.holdem4j.player_logic.player;

import org.json.JSONArray;
import org.json.JSONObject;
import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.holdem4j.player_logic.enums.PLAYER_ACTION;
import org.quazarspirit.holdem4j.player_logic.enums.PLAYER_INTENT;
import org.quazarspirit.holdem4j.room_logic.DEALER_INTENT;
import org.quazarspirit.holdem4j.room_logic.Table;
import org.quazarspirit.utils.publisher_subscriber_pattern.Event;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Bot implementation of Player
 * Stays exclusively on server in same process
 */
public class BotPlayer extends Player {
    /**
     * Create Bot player with Random UUID and defined username
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
        if(eventData.get("type") == DEALER_INTENT.QUERY_ACTION
            && eventData.get("player") == this) {
           handleQueryAction(eventData);
        }
    }

    void handleQueryAction(JSONObject eventData) {
        JSONArray jsonArray = (JSONArray) eventData.get("allowed_actions");
        ArrayList<PLAYER_ACTION> allowedActions = new ArrayList<PLAYER_ACTION>();

        for(Object obj: jsonArray) {
            PLAYER_ACTION playerAction = (PLAYER_ACTION) obj;
            allowedActions.add(playerAction);
        }

        Table table = (Table) eventData.get("table");
        Game game = table.getGame();

        PLAYER_ACTION key;
        if (allowedActions.contains(PLAYER_ACTION.CHECK)) {
            key = PLAYER_ACTION.CHECK;
        } else {
            key = PLAYER_ACTION.FOLD;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", PLAYER_INTENT.ACT);
    }


}
