package org.quazarspirit.holdem4j.room_logic;

import org.quazarspirit.holdem4j.game_logic.Pot;
import org.quazarspirit.holdem4j.game_logic.card_pile.Board;
import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.holdem4j.game_logic.Round;
import org.quazarspirit.holdem4j.game_logic.card_pile.ICardPile;
import org.quazarspirit.holdem4j.game_logic.card_pile.NullCardPile;
import org.quazarspirit.holdem4j.game_logic.card_pile.PocketCards;
import org.quazarspirit.holdem4j.room_logic.player_logic.IPlayer;
import org.quazarspirit.holdem4j.room_logic.player_logic.NullPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Table {
    private int _maxPlayerCount = 0;
    protected HashMap<PositionHandler.POSITION_NAME, IPlayer> players = new HashMap<>();
    protected HashMap<IPlayer, PocketCards> playersPocketCard = new HashMap<>();
    protected boolean isOpened = false;
    final private Round _round = new Round();
    final private Dealer _dealer = new Dealer(this);
    final private Board _board = new Board();

    final private Pot _pot = new Pot(Game.BET_STRUCTURE.NO_LIMIT);
    final private PositionHandler _positionHandler = new PositionHandler();
    final Game _game; // = new Game(Game.VARIANT.HOLDEM, Game.BET_STRUCTURE.NO_LIMIT);
    Table(int maxPlayerCount, Game game) {
        this._maxPlayerCount = maxPlayerCount;
        this._game = game;
    }

    void run() {
        do {
            _dealer.run();
            _round.next();
        } while(players.size() > 1);
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
    public Round getRound() { return _round; }
    public void setPlayerPocketCards(IPlayer player, PocketCards newPocketCards) {
        playersPocketCard.replace(player, newPocketCards);
    }
    public ArrayList<PositionHandler.POSITION_NAME> getUsedPositions() { return _positionHandler.getUsedPositions();}
    boolean addPlayer(IPlayer player) {
        if  (players.size() + 1 > _maxPlayerCount
                || _contains(player)) {
            return false;
        }

        players.put(_positionHandler.pickFreePosition(), player);
        playersPocketCard.put(player, new PocketCards());
        return true;
    }
    boolean removePlayer(IPlayer player) {
        if (!_contains(player)) { return false; }

        PositionHandler.POSITION_NAME positionToRemove = getPosition(player);
        players.remove(positionToRemove);
        playersPocketCard.remove(player);
        _positionHandler.releasePosition(positionToRemove);
        return true;
    }
    public int getPlayerCount() { return players.size(); }
    public int getMaxPlayerCount() { return _maxPlayerCount; }
    public IPlayer getPlayerFromPosition(PositionHandler.POSITION_NAME position_name) {
        IPlayer player = players.get(position_name);
        if (player == null) {
            return NullPlayer.GetSingleton();
        }
        return player;
    }
    public PositionHandler.POSITION_NAME getPosition(IPlayer concretePlayer) {
        for (Map.Entry<PositionHandler.POSITION_NAME, IPlayer> e: players.entrySet()) {
            if (e.getValue().equals(concretePlayer)) {
                return e.getKey();
            }
        }

        return PositionHandler.POSITION_NAME.NONE;
    }
    public Board getBoard() { return _board; }
    private boolean _contains(IPlayer playerToCheck) {
        for (IPlayer player : players.values()) {
            if (player == playerToCheck) {
                return true;
            }
        }
        return false;
    }
}
