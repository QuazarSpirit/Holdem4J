package org.quazarspirit.holdem4j.player_logic;

import java.util.HashMap;
import java.util.UUID;

public class PlayerRegistry {
    static HashMap<String, IPlayer> _players = new HashMap<>();

    /**
     * Method that adds player to player registry
     * @param identity Identity here is just username:password in a string
     * TODO: Prevent user username to be same as NullPlayer.NULL_USERNAME
     */
    static boolean addPlayer(String identity) {
        if (_players.get(identity) != null) {
            return false;
        }
    /// TODO: WARNING
        //_players.put(identity, new PlayerProxy(UUID.randomUUID(), identity.split(":")[0]));
        return true;
    }

    /**
     * @param identity username:password
     * @return IPlayer
     */
    static IPlayer getPlayer(String identity) {
        IPlayer player = _players.get(identity);
        if (player == null) {
            return NullPlayer.GetSingleton();
        }

        return player;
    }
}
