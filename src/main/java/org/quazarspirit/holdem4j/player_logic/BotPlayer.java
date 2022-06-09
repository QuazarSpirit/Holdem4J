package org.quazarspirit.holdem4j.player_logic;

import org.quazarspirit.holdem4j.game_logic.Bet;
import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.utils.KV;

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
        System.out.println(allowedActions + " " + username);
        PLAYER_ACTION key;
        if (allowedActions.contains(PLAYER_ACTION.CHECK)) {
            key = PLAYER_ACTION.CHECK;
        } else {
            key = PLAYER_ACTION.FOLD;
        }

        return new KV<PLAYER_ACTION, Bet>(key, new Bet(gameArg));
    }
}
