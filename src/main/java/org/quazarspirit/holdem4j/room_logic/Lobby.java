package org.quazarspirit.holdem4j.room_logic;

import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.holdem4j.player_logic.IPlayer;
import org.quazarspirit.holdem4j.view.LogTableView;

import java.util.*;

/**
 * Lobby is charged to create new tables for defined game structures
 * Add player to these tables
 * Create bot to fill those tables
 */
public class Lobby {
    private final ArrayList<Game> _games = new ArrayList<>() {{
        add(new Game(Game.VARIANT.TEXAS_HOLDEM, Game.BET_STRUCTURE.NO_LIMIT, 10));
    }};
    private final HashMap<Game, ArrayList<Table>> _gameToTables = new HashMap<>();
    Lobby() {
        for(Game game: _games) {
            _gameToTables.put(game, new ArrayList<Table>());
        }
    }

    /**
     * Add specified game to _games if not exists and create
     * new ArrayList of Tables for it in _gameToTables.
     * @param game Game structure to add to _games list and _gameToTables
     */
    public void addGame(Game game) {
        boolean gameFoundInGames = false;
        for(Game g: _games) {
            if (g.equals(game)){
                gameFoundInGames = true;
                break;
            }
        }

        boolean gameFoundInGameToTables = false;
        for(Game g: _gameToTables.keySet()) {
            if (g.equals(game)) {
                gameFoundInGameToTables = true;
                break;
            }
        }

        if (! gameFoundInGames) {
            _games.add(game);
            if (! gameFoundInGameToTables) {
                _gameToTables.put(game, new ArrayList<Table>());
            }
        }
    }

    /**
     * Clear all Lists currently used for unit testing only
     */
    public void clearGames() { _games.clear(); _gameToTables.clear(); }

    /**
     * Add specified player to first available table if game is not defined,
     * nothing happens
     * @param player Player to add on table
     * @param game Game to lookup on _gameToTables hashmap
     */
    public void joinGame(IPlayer player, Game game) {
        Table availableTable = getAvailableTable(game);

        if (availableTable != null) {
            availableTable.addPlayer(player);
        }
    }

    /**
     * @return List of Authorized games
     */
    public ArrayList<Game> getGames() {
        return _games;
    }

    /**
     * Look up for available table in _gameToTables hashmap if game is not found is _games return null
     * Otherwise return available table or create a new one if nothing is found;
     * @param game Game to look for in _gameToTables hashmap
     * @return First available table (opened) or null
     */
    public Table getAvailableTable(Game game) {
        if (_games.contains(game)) {
            if(_gameToTables.containsKey(game)) {
                ArrayList<Table> tables = _gameToTables.get(game);

                boolean foundTable = false;
                Table availableTable = null;
                for(Table table: tables) {
                    if (table.isOpened) {
                        foundTable = true;
                        availableTable = table;
                        break;
                    }
                }

                if (foundTable) {
                    return availableTable;
                } else {
                    Table table = new Table(game);
                    LogTableView logTableView = new LogTableView();
                    table.addSubscriber(logTableView);

                    _gameToTables.get(game).add(table);
                    return table;
                }
            }
            else {
                System.out.println("Lobby game to table hashmap DOES NOT contains specified game");
            }
        }
        return null;
    }

    /**
     * String representation of lobby used for monitoring and debug
     * @return Game as string and sub table players for specified games
     */
    public String asString() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<Game, ArrayList<Table>> set: _gameToTables.entrySet()) {
            result.append("-----------------------------\n");
            result.append(set.getKey().asString());

            for (Table table: set.getValue()) {
                result.append("\n -- Player count: ").append(table.getPlayerCount()).append(" ");
                for(IPlayer p: table.players.values()) {
                    result.append(p.getUsername()).append(" - ");
                }
            }
        }

        return result.toString();
    }
}
