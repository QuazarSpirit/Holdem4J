package org.quazarspirit.holdem4j.room_logic;

import org.junit.jupiter.api.Test;
import org.quazarspirit.holdem4j.TestLifecycle;
import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.holdem4j.player_logic.player.BotPlayer;
import org.quazarspirit.utils.Utils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class LobbyTest extends TestLifecycle {
    LobbyTest() {
        System.setProperty("TEST", "true");
    }
    static final Game testGame = new Game(Game.VARIANT.TEXAS_HOLDEM, Game.BET_STRUCTURE.NO_LIMIT, Game.PLAYER_TYPE.AI);
    public Lobby createLobby() {
        Lobby lobby = Lobby.getSingleton();
        lobby.clearGames();
        lobby.addGame(testGame);

        return lobby;
    }

    @Test
    void constructor() {
        Lobby lobby = createLobby();
        assertNotEquals(lobby, null);
    }

    @Test
    void joinGame() {
        Lobby lobby = createLobby();
        final int maxPlayersCount = 10;
        for(int i = 0; i < maxPlayersCount; i++) {
            lobby.joinGame(new BotPlayer(UUID.randomUUID(), "Bot_" + i), testGame);
        }

        Utils.Log(lobby.asString());
        //assertEquals(maxPlayersCount, lobby.getAvailableTable(testGame).getPlayerCount());
    }

    @Test
    void getGames() {
    }

    @Test
    void main() {
    }
}