package org.quazarspirit.holdem4j.room_logic;

import org.junit.jupiter.api.Test;
import org.quazarspirit.holdem4j.TestLifecycle;
import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.holdem4j.game_logic.BettingRound;
import org.quazarspirit.holdem4j.game_logic.card_pile.ICardPile;
import org.quazarspirit.holdem4j.player_logic.player.BotPlayer;
import org.quazarspirit.holdem4j.player_logic.player.IPlayer;
import org.quazarspirit.holdem4j.view.LogTableView;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TableTest extends TestLifecycle {
    public static final Game testGame = new Game(Game.VARIANT.TEXAS_HOLDEM, Game.BET_STRUCTURE.NO_LIMIT, 100, Game.MAX_SEATS.FULL_RING);

    TableTest() {
        System.setProperty("TEST", "true");
    }

    static Table initTableWithPlayers(ArrayList<IPlayer> iPlayers, int count, Game game, boolean logTable) {
        Table table = new Table(game);

        if (logTable) {
            LogTableView logTableView = new LogTableView();
            table.addSubscriber(logTableView);
        }

        for (int i = 0; i < count; i+=1) {
            BotPlayer bot = new BotPlayer(UUID.randomUUID(), "Bot-" + i);
            table.addPlayer(bot);
            iPlayers.add(bot);
        }

        //Utils.Log("Count: " + count + " table count: " + table.getPlayerCount());
        //Utils.Log(table.getRound().getPhase() + " " + table.isOpened);
        //Utils.Log(table + " " + iPlayers);

        return table;
    }

    static Table initTableWithPlayers(ArrayList<IPlayer> iPlayers, int count, Game game) {
        return initTableWithPlayers(iPlayers, count, game, true);
    }

    @Test
    void nextRoundPhase() {
        ArrayList<IPlayer> iPlayers = new ArrayList<>();
        Table table = TableTest.initTableWithPlayers(iPlayers, 3, testGame);

        for(int i = 0; i < BettingRound.PHASE.values().length; i += 1) {
            assertEquals(BettingRound.PHASE.values()[i], table.getRound().getPhase());
            table.nextBettingRoundPhase();
        }
    }

    @Test
    void addPlayer() {
        for (int i = 1; i < testGame.getMaxSeatsCount(); i += 1) {
            ArrayList<IPlayer> iPlayers = new ArrayList<>();
            Table table = TableTest.initTableWithPlayers(iPlayers, i, testGame);

            for (int j = 0; j < i; j += 1) {
                assertEquals(table.getPlayerFromSeatNumber(j), iPlayers.get(j));
            }
        }
    }

    @Test
    void removePlayer() {
        for (int i = 1; i < testGame.getMaxSeatsCount(); i += 1) {
            ArrayList<IPlayer> iPlayers = new ArrayList<>();
            Table table = TableTest.initTableWithPlayers(iPlayers, i, testGame);

            for(int j = 0; j < i; j+=1) {
                IPlayer player = iPlayers.get(j);
                assertEquals(table.getPlayerCount(), iPlayers.size() - j);
                table.removePlayer(player);
                assertEquals(table.getPlayerCount(), iPlayers.size() - j - 1);
            }
        }
    }

    @Test
    void getPocketCards() {
        for (int i = 1; i < testGame.getMaxSeatsCount(); i += 1) {
            ArrayList<IPlayer> iPlayers = new ArrayList<>();
            Table table = TableTest.initTableWithPlayers(iPlayers, i, testGame);

            ICardPile pocketCards = table.getPocketCards(iPlayers.get(i - 1));
            assertTrue(pocketCards.isEmpty());
        }
    }
}