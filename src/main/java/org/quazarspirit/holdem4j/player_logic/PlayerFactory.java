package org.quazarspirit.holdem4j.player_logic;

import java.util.UUID;

public class PlayerFactory {
    enum PLAYER_TYPE {
        REAL_PLAYER, BOT_PLAYER
    }
    static IPlayer create(UUID uuid, String username, String botCondition) {
        return new RealPlayer(uuid, username);
    }
}
