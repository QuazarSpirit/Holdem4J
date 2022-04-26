package com.jne.holdem4j.room_logic;

import com.jne.holdem4j.room_logic.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerRegistry {
    static HashMap<String, Player> _players = new HashMap<String, Player>();

    /**
     * Method that adds player to player registry
     * @param identity Identity here is just username:password in a string
     */
    static boolean addPlayer(String identity) {
        if (_players.get(identity) != null) {
            return false;
        }

        _players.put(identity, new Player(UUID.randomUUID()));
        return true;
    }

    /**
     * TODO: NULL_PLAYER
     * @param identity
     * @return
     */
    static Player getPlayer(String identity) {
        Player player = _players.get(identity);
        if (player == null) {
            return null;
        }

        return player;
    }
}
