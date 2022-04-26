package com.jne.holdem4j.room_logic;

import com.jne.holdem4j.game_logic.Game;

import java.util.HashMap;
import java.util.Map;

class Table {
    private int _maxPlayerCount = 0;
    protected HashMap<PositionHandler.POSITION_NAME, Player> _players =
            new HashMap<PositionHandler.POSITION_NAME, Player>();

    protected boolean isOpened = false;

    final private PositionHandler _positionHandler = new PositionHandler();
    final Game _game; // = new Game(Game.VARIANT.HOLDEM, Game.BET_STRUCTURE.NO_LIMIT);

    Table(int maxPlayerCount, Game game) {
        this._maxPlayerCount = maxPlayerCount;
        this._game = game;
    }

    boolean addPlayer(Player player) {
        if  (_players.size() + 1 > _maxPlayerCount
            || _contains(player)) {
            return false;
        }

        _players.put( _positionHandler.pickFreePosition(), player);
        return true;
    }

    boolean removePlayer(Player player) {
        if (!_contains(player)) { return false; }

        PositionHandler.POSITION_NAME positionToRemove = getPosition(player);
        _players.remove(positionToRemove);
        _positionHandler.releasePosition(positionToRemove);
        return true;
    }

    public PositionHandler.POSITION_NAME getPosition(Player player) {
        for (Map.Entry<PositionHandler.POSITION_NAME, Player> e:_players.entrySet()) {
            if (e.getValue().equals(player)) {
                return e.getKey();
            }
        }

        return PositionHandler.POSITION_NAME.NONE;
    }

    int getMaxPlayerCount() {
        return _maxPlayerCount;
    }

    int getPlayerCount() {
        return _players.size();
    }

    private boolean _contains(Player playerToCheck) {
        for (Player player:_players.values()) {
            if (player == playerToCheck) {
                return true;
            }
        }
        return false;
    }
}
