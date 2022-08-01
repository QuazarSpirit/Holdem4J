package org.quazarspirit.holdem4j.room_logic;

import org.junit.jupiter.api.Test;
import org.quazarspirit.holdem4j.TestLifecycle;
import org.quazarspirit.holdem4j.player_logic.player.BotPlayer;
import org.quazarspirit.holdem4j.player_logic.player.IPlayer;
import org.quazarspirit.holdem4j.player_logic.player_seat.IPlayerSeat;
import org.quazarspirit.utils.Utils;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerSeatRegistryTest extends TestLifecycle {

    PlayerSeatRegistryTest() {
        System.setProperty("TEST", "true");
    }

    @Test
    void constructor() {
        PlayerSeatRegistry registry = new PlayerSeatRegistry(TableTest.testGame);
        assertEquals(TableTest.testGame.getMaxSeatsCount(), registry.getPlayersSeat().size());
        Utils.Log(registry.asString());

    }

    @Test
    void addPlayers() {
        PlayerSeatRegistry registry = new PlayerSeatRegistry(TableTest.testGame);

        for (int i = 0; i < TableTest.testGame.getMaxSeatsCount(); i += 1) {
            IPlayer bot = new BotPlayer(UUID.randomUUID(), "Bot-" + i);
            assertTrue(registry.add(bot));

            assertEquals(bot, registry.getPlayersSeat().get(i).getKey());
        }

        Utils.Log(registry.asString());
    }

    @Test
    void addPlayersOnFullTable() {
        PlayerSeatRegistry registry = new PlayerSeatRegistry(TableTest.testGame);

        for (int i = 0; i < TableTest.testGame.getMaxSeatsCount(); i += 1) {
            IPlayer bot = new BotPlayer(UUID.randomUUID(), "Bot-" + i);
            assertTrue(registry.add(bot));

            assertEquals(bot, registry.getPlayersSeat().get(i).getKey());
        }

        IPlayer bot = new BotPlayer(UUID.randomUUID(), "Bot-beyond");
        assertFalse(registry.add(bot));


        Utils.Log(registry.asString());
    }

    @Test
    void removeOneRandomPlayer() {

        Random rand = new Random();

        for (int i = 0; i < 20; i += 1) {
            PlayerSeatRegistry registry = new PlayerSeatRegistry(TableTest.testGame);
            ArrayList<IPlayer> players = new ArrayList<>();
            for (int j = 0; j < TableTest.testGame.getMaxSeatsCount(); j += 1) {
                IPlayer bot = new BotPlayer(UUID.randomUUID(), "Bot-" + j);
                registry.add(bot);
                players.add(bot);
            }

            IPlayer randomPlayer = players.get(rand.nextInt(players.size() - 1));
            assertTrue(registry.remove(randomPlayer));
            Utils.Log(registry.asString());
        }
    }

    @Test
    void removeUnknownPlayer() {
        PlayerSeatRegistry registry = new PlayerSeatRegistry(TableTest.testGame);
        for (int i = 0; i < TableTest.testGame.getMaxSeatsCount(); i += 1) {
            IPlayer bot = new BotPlayer(UUID.randomUUID(), "Bot-" + i);
            assertTrue(registry.add(bot));

            assertEquals(bot, registry.getPlayersSeat().get(i).getKey());
        }

        IPlayer bot = new BotPlayer(UUID.randomUUID(), "Bot-unknown");
        assertFalse(registry.remove(bot));

        Utils.Log(registry.asString());
    }

    @Test
    void removePlayerOnEmptyTable() {
        PlayerSeatRegistry registry = new PlayerSeatRegistry(TableTest.testGame);

        IPlayer bot = new BotPlayer(UUID.randomUUID(), "Bot-beyond");
        assertFalse(registry.remove(bot));


        Utils.Log(registry.asString());
    }

    @Test
    void fillRegistryAndRemoveRandomPlayer() {
        Random rand = new Random();

        for (int i = 0; i < 20; i += 1) {
            PlayerSeatRegistry registry = new PlayerSeatRegistry(TableTest.testGame);
            ArrayList<IPlayer> players = new ArrayList<>();
            for (int j = 0; j < TableTest.testGame.getMaxSeatsCount(); j += 1) {
                IPlayer bot = new BotPlayer(UUID.randomUUID(), "Bot-" + j);
                registry.add(bot);
                players.add(bot);
            }

            IPlayer randomPlayer = players.get(rand.nextInt(players.size() - 1));
            assertTrue(registry.remove(randomPlayer));
            Utils.Log(registry.asString());
        }
    }

    @Test
    public void bindPositionsForTableRound1() {
        for (int k = 0; k < TableTest.testGame.getMaxSeatsCount(); k += 1) {
            ArrayList<IPlayer> players = new ArrayList<>();
            Table table = TableTest.initTableWithPlayers(players, k, TableTest.testGame);
            PlayerSeatRegistry registry = table._playerSeats;

            registry.bindPositions(table._tableRoundCounter);

            for (int i = 0; i < table.getPlayerCount(); i += 1) {
                IPlayer player = registry.getPlayerFromSeatNumber(i);
                IPlayerSeat playerSeat = registry.getSeatFromPlayer(player);
                assertEquals(playerSeat.getPosition(), registry._positionHandler.getUsed().get(i));
            }

            //Utils.Log(registry.asString());
        }
    }

    @Test
    public void bindPositionsForMultipleTableRounds() {
        for (int k = 0; k < TableTest.testGame.getMaxSeatsCount(); k += 1) {
        ArrayList<IPlayer> players = new ArrayList<>();
        Table table = TableTest.initTableWithPlayers(players, k, TableTest.testGame, false);
        PlayerSeatRegistry registry = table._playerSeats;
            for(int i = 0; i < 25; i += 1) {
                    registry.bindPositions(table._tableRoundCounter);

                    for (int l = 0; l < table.getPlayerCount(); l += 1) {
                        int shift = table._tableRoundCounter % registry.size();
                        IPlayer player = registry.getPlayerFromSeatNumber(l);
                        IPlayerSeat playerSeat = registry.getSeatFromPlayer(player);
                        assertEquals(playerSeat.getPosition(), registry._positionHandler.getUsed().get(l + shift));
                    }

                    table.resetBettingRoundPhase();
                    table.nextBettingRoundPhase();
                    //Utils.Log(table._tableRoundCounter);
                    //Utils.Log("---------------------------\n" + registry.asString(PlayerSeatRegistry.FILTER.BOUND_SEAT));

            }
        }
    }
}