package org.quazarspirit.holdem4j.room_logic;

import org.json.JSONObject;
import org.quazarspirit.holdem4j.game_logic.Pot;
import org.quazarspirit.holdem4j.game_logic.card_pile.Board;
import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.holdem4j.game_logic.Round;
import org.quazarspirit.holdem4j.game_logic.card_pile.ICardPile;
import org.quazarspirit.holdem4j.game_logic.card_pile.NullCardPile;
import org.quazarspirit.holdem4j.game_logic.card_pile.PocketCards;
import org.quazarspirit.holdem4j.player_logic.IPlayer;
import org.quazarspirit.holdem4j.player_logic.NullPlayer;
import org.quazarspirit.holdem4j.player_logic.PLAYER_INTENT;
import org.quazarspirit.utils.ImmutableKV;
import org.quazarspirit.utils.publisher_subscriber_pattern.Publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Table extends Publisher {
    private final int _maxPlayerCount;
    protected boolean isOpened = true;
    protected HashMap<Position.NAME, IPlayer> players = new HashMap<>();
    protected HashMap<IPlayer, PocketCards> playersPocketCard = new HashMap<>();
    protected HashMap<IPlayer, Integer> playersStack = new HashMap<>();
    final private ArrayList<ImmutableKV<IPlayer, PLAYER_INTENT>> _waitingPlayers = new ArrayList<>();
    private boolean _isInStasis = true;
    final private Dealer _dealer;
    final private Round _round = new Round();
    final private Board _board = new Board();
    final private Position _positionHandler = new Position();
    final private Pot _pot;
    final private Game _game; // = new Game(Game.VARIANT.HOLDEM, Game.BET_STRUCTURE.NO_LIMIT);
    Table(Game game) {
        _game = game;
        _maxPlayerCount = _game.getMaxPlayerCount();
        _pot = new Pot(_game.getBetStructure());
        _dealer = new Dealer(this);
    }
    public void nextRoundPhase() {
        System.out.println("------------\n " + _round.getRoundPhase().toString());
        _round.next();
        _isInStasis = (_round.getRoundPhase() == Round.ROUND_PHASE.STASIS);
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
    public Dealer getDealer() { return _dealer; }
    public void setPlayerPocketCards(IPlayer player, PocketCards newPocketCards) {
        playersPocketCard.replace(player, newPocketCards);
    }
    public ArrayList<Position.NAME> getUsedPositions() { return _positionHandler.getUsed(); }
    public ArrayList<Position.NAME> getPlayingPositions() { return _positionHandler.getPlaying(); }
    public void addPlayer(IPlayer player) {
        // Test if player is not already connected to table
        if (_contains(player)) { return; }

        // Test if futurePlayer is superior to max allowed player count
        int futurePlayerCount = players.size() + 1;
        if (futurePlayerCount >= _maxPlayerCount) {
            isOpened = false;
            if (futurePlayerCount > _maxPlayerCount || _contains(player)) {
                return;
            }
        }

        if (_round.getRoundPhase() == Round.ROUND_PHASE.STASIS) {
            _positionHandler.update(_maxPlayerCount, players.size());
            Position.NAME freePosition = _positionHandler.pickFree();
            players.put(freePosition, player);
        } else {
            _waitingPlayers.add(new ImmutableKV<IPlayer, PLAYER_INTENT>(player, PLAYER_INTENT.JOIN));
        }

        playersPocketCard.put(player, new PocketCards());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", PLAYER_INTENT.JOIN);
        publish(jsonObject);
    }
    public void removePlayer(IPlayer player) {
        // ONLY if CASH_GAME
        isOpened = true;
        if (!_contains(player)) { return; }


        if (_round.getRoundPhase() == Round.ROUND_PHASE.STASIS) {
            Position.NAME positionToRemove = getPositionFromPlayer(player);
            players.remove(positionToRemove);
            playersPocketCard.remove(player);
            _positionHandler.release(positionToRemove);
        } else {
            _waitingPlayers.add(new ImmutableKV<IPlayer, PLAYER_INTENT>(player, PLAYER_INTENT.LEAVE));
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", PLAYER_INTENT.LEAVE);
        publish(jsonObject);
    }
    public int getPlayerCount() { return players.size(); }
    public int getMaxPlayerCount() { return _maxPlayerCount; }
    public IPlayer getPlayerFromPosition(Position.NAME positionName) {
        if (players.containsKey(positionName)) {
            return players.get(positionName);
        }

        return NullPlayer.GetSingleton();
    }
    public Position.NAME getPositionFromPlayer(IPlayer concretePlayer) {
        for (Map.Entry<Position.NAME, IPlayer> e: players.entrySet()) {
            if (e.getValue().equals(concretePlayer)) {
                return e.getKey();
            }
        }

        return Position.NAME.NONE;
    }
    public void foldPosition(Position.NAME foldPositionName) {
        _positionHandler.releasePlaying(foldPositionName);
    }
    public Board getBoard() { return _board; }
    public Game getGame() { return _game; }
    public Pot getPot() { return _pot; }

    /**
     * Mandatory payment of blinds by players
     */
    public int blindBet() {
        IPlayer player_SB = getPlayerFromPosition(Position.NAME.SB);
        IPlayer player_BB = getPlayerFromPosition(Position.NAME.BB);

        int BB = _game.getBB();
        int SB = BB / 2;
        /* TODO: Implements banking system
        playersStack.put(player_SB, playersStack.get(player_SB) - SB);
        playersStack.put(player_BB,  playersStack.get(player_BB) - BB);
         */

        return BB + SB;

    }
    private boolean _contains(IPlayer playerToCheck) {
        for (IPlayer player : players.values()) {
            if (player.equals(playerToCheck)) {
                return true;
            }
        }
        return false;
    }
}
