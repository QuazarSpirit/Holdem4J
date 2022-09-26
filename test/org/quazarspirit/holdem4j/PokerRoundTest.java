package org.quazarspirit.holdem4j;

import org.junit.jupiter.api.Test;
import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.holdem4j.player_logic.player.BotPlayer;
import org.quazarspirit.holdem4j.player_logic.player.IPlayer;
import org.quazarspirit.holdem4j.room_logic.Table;

import java.util.ArrayList;

public class PokerRoundTest {
    private ArrayList<IPlayer> createBots(int number) {
        ArrayList<IPlayer> players = new ArrayList<>();

        for (int i = 0; i < number; i++) {
            IPlayer player = new BotPlayer(number + "");
            players.add(player);
        }

       return players;
    }

    @Test
    public void fullRoundWithBots() {
        Game game = new Game();
        Table table = new Table(game);

        ArrayList<IPlayer> players = createBots(6);
        for (IPlayer player: players) {
            table.addPlayer(player);
        }
    }
}
