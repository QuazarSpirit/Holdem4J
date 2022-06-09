package org.quazarspirit.holdem4j.room_logic;

import org.junit.jupiter.api.Test;
import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.holdem4j.game_logic.Round;
import org.quazarspirit.holdem4j.game_logic.card_pile.ICardPile;
import org.quazarspirit.holdem4j.player_logic.BotPlayer;
import org.quazarspirit.holdem4j.player_logic.IPlayer;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TableTest {
    static final Game testGame = new Game(Game.VARIANT.TEXAS_HOLDEM, Game.BET_STRUCTURE.NO_LIMIT, 100, Game.MAX_SEATS.FULL_RING);

    static Table initTableWithPlayers(ArrayList<IPlayer> iPlayers, int count, Game game) {
        Table table = new Table(game);

        for (int i = 0; i < count; i+=1) {
            BotPlayer bot1 = new BotPlayer(UUID.randomUUID(), "Bot");
            table.addPlayer(bot1);
        }

        for(Position.NAME positionName: table.getUsedPositions()) {
            iPlayers.add(table.getPlayerFromPosition(positionName));
        }

        return table;
    }

    @Test
    void nextRoundPhase() {
        Table table = new Table(testGame);

        for(int i = 0; i < Round.ROUND_PHASE.values().length; i += 1) {
            assertEquals(table.getRound().getRoundPhase(), Round.ROUND_PHASE.values()[i]);
            table.nextRoundPhase();
        }
    }

    @Test
    void addPlayer() {
        ArrayList<IPlayer> iPlayers = new ArrayList<>();
        Table table = TableTest.initTableWithPlayers(iPlayers, 3, testGame);

        assertEquals(table.getPlayerFromPosition(Position.NAME.SB), iPlayers.get(0));
        assertEquals(table.getPlayerFromPosition(Position.NAME.BB), iPlayers.get(1));
    }

    @Test
    void removePlayer() {
        ArrayList<IPlayer> iPlayers = new ArrayList<>();
        Table table = TableTest.initTableWithPlayers(iPlayers, 3, testGame);

        for(int i = 0; i < iPlayers.size(); i+=1) {
            IPlayer player = iPlayers.get(i);
            assertEquals(table.getPlayerCount(), iPlayers.size() - i);
            table.removePlayer(player);
            assertEquals(table.getPlayerCount(), iPlayers.size() - i - 1);

        }
    }

    @Test
    void getPocketCards() {
        ArrayList<IPlayer> iPlayers = new ArrayList<>();
        Table table = TableTest.initTableWithPlayers(iPlayers, 3, testGame);

        ICardPile pocketCards = table.getPocketCards(iPlayers.get(0));
        assertTrue(pocketCards.isEmpty());
    }
}