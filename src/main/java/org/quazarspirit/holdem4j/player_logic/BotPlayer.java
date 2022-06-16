package org.quazarspirit.holdem4j.player_logic;

import org.json.JSONArray;
import org.json.JSONObject;
import org.quazarspirit.holdem4j.game_logic.Bet;
import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.holdem4j.room_logic.DEALER_INTENT;
import org.quazarspirit.holdem4j.room_logic.Table;
import org.quazarspirit.utils.KV;
import org.quazarspirit.utils.publisher_subscriber_pattern.Event;
import org.quazarspirit.utils.publisher_subscriber_pattern.ISubscriber;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Bot implementation of Player
 * Stays exclusively on server in same process
 */
public class BotPlayer extends Player {
    public BotPlayer(UUID uuid, String username) {
        super(uuid, username);
    }

    // TODO: Implements strategy design pattern
    @Override
    public KV<PLAYER_ACTION, Bet> queryAction(ArrayList<PLAYER_ACTION> allowedActions, Game gameArg) {
        //System.out.println(allowedActions + " " + username);
        PLAYER_ACTION key;
        if (allowedActions.contains(PLAYER_ACTION.CHECK)) {
            key = PLAYER_ACTION.CHECK;
        } else {
            key = PLAYER_ACTION.FOLD;
        }

        return new KV<PLAYER_ACTION, Bet>(key, new Bet(gameArg));
    }

    /**
     * @param event
     */
    @Override
    public void update(Event event) {
        JSONObject eventData = event.data;
        if(eventData.get("type") == DEALER_INTENT.QUERY_ACTION
            && eventData.get("player") == this) {
            JSONArray jsonArray = (JSONArray) eventData.get("allowed_actions");
            ArrayList<PLAYER_ACTION> allowedActions = new ArrayList<PLAYER_ACTION>();

            for(Object obj: jsonArray) {
                PLAYER_ACTION playerAction = (PLAYER_ACTION) obj;
                allowedActions.add(playerAction);
            }

            Table table = (Table) eventData.get("table");
            Game game = table.getGame();
            queryAction(allowedActions, game);

        }
    }


}
