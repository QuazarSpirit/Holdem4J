package org.quazarspirit.holdem4j.room_logic;

import org.quazarspirit.holdem4j.game_logic.card_pile.Board;
import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.holdem4j.game_logic.Round;
import org.quazarspirit.holdem4j.game_logic.card_pile.ICardPile;
import org.quazarspirit.holdem4j.game_logic.card_pile.NullCardPile;
import org.quazarspirit.holdem4j.game_logic.card_pile.PocketCards;
import org.quazarspirit.holdem4j.room_logic.player_logic.IPlayer;
import org.quazarspirit.holdem4j.room_logic.player_logic.NullPlayer;

import java.util.HashMap;
import java.util.Map;

public class Table {
    private int _maxPlayerCount = 0;
    protected HashMap<PositionHandler.POSITION_NAME, IPlayer> players =
        new HashMap<PositionHandler.POSITION_NAME, IPlayer>();

    protected HashMap<IPlayer, PocketCards> playersPocketCard =
        new HashMap<IPlayer, PocketCards>();

    protected boolean isOpened = false;
    final private Round _round = new Round();
    final private Board _board = new Board();
    final private PositionHandler _positionHandler = new PositionHandler();
    final Game _game; // = new Game(Game.VARIANT.HOLDEM, Game.BET_STRUCTURE.NO_LIMIT);

    Table(int maxPlayerCount, Game game) {
        this._maxPlayerCount = maxPlayerCount;
        this._game = game;
    }

    boolean addPlayer(IPlayer player) {
        if  (players.size() + 1 > _maxPlayerCount
            || _contains(player)) {
            return false;
        }

        players.put( _positionHandler.pickFreePosition(), player);
        return true;
    }

    boolean removePlayer(IPlayer player) {
        if (!_contains(player)) { return false; }

        PositionHandler.POSITION_NAME positionToRemove = getPosition(player);
        players.remove(positionToRemove);
        _positionHandler.releasePosition(positionToRemove);
        return true;
    }

    public PositionHandler.POSITION_NAME getPosition(IPlayer concretePlayer) {
        for (Map.Entry<PositionHandler.POSITION_NAME, IPlayer> e: players.entrySet()) {
            if (e.getValue().equals(concretePlayer)) {
                return e.getKey();
            }
        }

        return PositionHandler.POSITION_NAME.NONE;
    }

    public ICardPile getPocketCards(IPlayer player) {
        final ICardPile NCP = NullCardPile.GetSingleton();
        if (player.equals(NullPlayer.GetSingleton())) {
            return NCP;
        }

        ICardPile cardPile = playersPocketCard.get(player);
        if (cardPile == null) {
            return NCP;
        }

       return cardPile;
    }

    int getMaxPlayerCount() {
        return _maxPlayerCount;
    }

    public Round getRound() { return _round; }

    int getPlayerCount() {
        return players.size();
    }

    public Board getBoard() { return _board; }
    public IPlayer getPlayerFromPosition(PositionHandler.POSITION_NAME position_name) {
        IPlayer player = players.get(position_name);
        if (player == null) {
            return NullPlayer.GetSingleton();
        }
        return player;
    }
    private boolean _contains(IPlayer playerToCheck) {
        for (IPlayer player : players.values()) {
            if (player == playerToCheck) {
                return true;
            }
        }
        return false;
    }
}
