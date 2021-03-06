package org.quazarspirit.holdem4j.room_logic;

import org.json.JSONObject;
import org.quazarspirit.utils.ImmutableKV;
import org.quazarspirit.utils.Utils;
import org.quazarspirit.utils.publisher_subscriber_pattern.*;
import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.holdem4j.game_logic.BettingRound;
import org.quazarspirit.holdem4j.player_logic.player.IPlayer;
import org.quazarspirit.holdem4j.player_logic.player.NullPlayer;
import org.quazarspirit.holdem4j.game_logic.chip_pile.Pot;
import org.quazarspirit.holdem4j.game_logic.chip_pile.Stack;
import org.quazarspirit.holdem4j.game_logic.card_pile.Board;
import org.quazarspirit.holdem4j.game_logic.card_pile.ICardPile;
import org.quazarspirit.holdem4j.game_logic.card_pile.NullCardPile;
import org.quazarspirit.holdem4j.game_logic.card_pile.PocketCards;
import org.quazarspirit.holdem4j.player_logic.enums.PLAYER_INTENT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Table extends Thread implements ITable, ISubscriber, IPublisher {
    public enum EVENT implements IEventType {
        TIMEOUT, POT_ADD, POT_RESET;
    }

    protected int _tableRoundCounter = 0;

    private final int _maxPlayerCount;
    protected boolean isOpened = true;

    protected final PlayerSeatRegistry _playerSeats;


    /**
     * @deprecated  replaced by {@link #_playerSeats}
     */
    @Deprecated
    protected HashMap<POSITION, IPlayer> players = new HashMap<>();

    protected HashMap<IPlayer, Stack> playersStack = new HashMap<>();
    protected HashMap<IPlayer, PocketCards> playersPocketCard = new HashMap<>();
    final private Dealer _dealer;
    final private BettingRound _bettingRound = new BettingRound();
    final private Board _board = new Board();
    final private Pot _pot;
    final private Game _game;

    final private Publisher _publisher = new Publisher(this);
    public Table(Game game) {
        _game = game;
        _maxPlayerCount = _game.getMaxSeatsCount();
        _pot = new Pot(_game.getUnit());
        _playerSeats = new PlayerSeatRegistry(_game);
        this.addSubscriber(_playerSeats);
        this.addSubscriber(_bettingRound);
        _dealer = new Dealer(this);
        /*
        _positionHandler = new PositionHandler();
        this.addSubscriber(_positionHandler);
         */
    }

    // TODO: Test with multiple complete rounds
    public void nextBettingRoundPhase() {
        //Utils.Log("------------\nCurrent phase: " + _bettingRound.getPhase().toString());
        ///_bettingRound.nextPhase();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", BettingRound.EVENT.NEXT);
        jsonObject.put("round_phase", _bettingRound.getPhase().getNext());
        publish(jsonObject);

        BettingRound.PHASE roundPhase = _bettingRound.getPhase();
        Utils.Log( _bettingRound.getPhase());

        if (roundPhase == BettingRound.PHASE.STASIS) {
            // Manage waiting players
            manageWaitingPlayers();
            // Update player seat
            updatePlayerSeat();
        } else if (roundPhase == BettingRound.PHASE.PRE_FLOP) {
            _tableRoundCounter+=1;

            updatePlayerPositions();

        }
    }

    public void resetBettingRoundPhase() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", BettingRound.EVENT.RESET);
        jsonObject.put("round_phase",BettingRound.PHASE.STASIS);
        publish(jsonObject);

        // Manage waiting players
        manageWaitingPlayers();
        // Update player seat
        updatePlayerSeat();
    }

    public void updatePlayerPositions() {
        //Utils.Log("Updating player positions");

        JSONObject bindPositionToSeatEventData = new JSONObject();
        bindPositionToSeatEventData.put("type", PlayerSeatRegistry.EVENT.BIND);
        bindPositionToSeatEventData.put("table_round_count", _tableRoundCounter);
        publish(bindPositionToSeatEventData);
    }

    public void updatePlayerSeat() {
        /* TODO: Debug
        Utils.Log("Updating player positions");
        // Loop over player seats and update their position names
        PositionHandler previousPositionHandler;

        JSONObject eventData = new JSONObject();
        eventData.put("type", PositionHandler.EVENT.ALLOCATE);
        eventData.put("max_player_count", _maxPlayerCount);
        eventData.put("player_count", _playersSeat.size());
        publish(eventData);

        for(Map.Entry<IPlayer, IPlayerSeat> kv: _playersSeat.entrySet()) {
            _positionHandler.getUsed();
            kv.getValue().setPosition();
        }

         */
    }

    public void manageWaitingPlayers() {
        //Utils.Log("Managing waiting players");

        // Make player leave / join if they want to
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

    public Stack getStack(IPlayer player) {
        // TODO: Implements
        return new Stack(_game.getUnit());
        /*
        final ICardPile NCP = NullCardPile.GetSingleton();
        if (player.equals(NullPlayer.GetSingleton())) {
            return NCP;
        }

        ChipCount chipCount = playersStack.get(player);
        if (chipCount == null) {
            return NCP;
        }

        return chipCount;
         */
    }

    public BettingRound getRound() { return _bettingRound; }
    public Dealer getDealer() { return _dealer; }
    public void setPlayerPocketCards(IPlayer player, PocketCards newPocketCards) {
        // Replace only if player is existing in array
        if (playersPocketCard.get(player) != null) {
            playersPocketCard.replace(player, newPocketCards);
        }
    }

    /**
     * @Deprecated Use events instead
     * @return
     */
    @Deprecated
    public ArrayList<POSITION> getUsedPositions() { return _playerSeats._positionHandler.getUsed(); }

    /**
     * @Deprecated Use events instead
     * @return
     */
    @Deprecated
    public ArrayList<POSITION> getPlayingPositions() { return _playerSeats._positionHandler.getPlaying(); }
    public void addPlayer(IPlayer player) {
        // Test if player is not already connected to table
        // or that the table is currently opened
        if (_playerSeats._containsPlayer(player) || !isOpened) { return; }

        // Test if futurePlayer is superior to max allowed player count
        int futurePlayerCount = players.size() + 1;
        if (futurePlayerCount >= _maxPlayerCount) {
            isOpened = false;
            if (futurePlayerCount > _maxPlayerCount) {
                return;
            }
        }


        if (_bettingRound.getPhase() == BettingRound.PHASE.STASIS) {
            _playerSeats.add(player);

            _dealer.addSubscriber(player);
            player.addSubscriber(_dealer);
        } else {
            //_waitingPlayers.add(new ImmutableKV<IPlayer, PLAYER_INTENT>(player, PLAYER_INTENT.JOIN));
        }

        playersPocketCard.put(player, new PocketCards());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", PLAYER_INTENT.JOIN);
        jsonObject.put("player", player);
        publish(jsonObject);
    }
    public void removePlayer(IPlayer player) {
        if (_game.getFormat() == Game.FORMAT.CASHGAME) {
            isOpened = true;
        }

        if (!_playerSeats._containsPlayer(player)) { return; }


        if (_bettingRound.getPhase() == BettingRound.PHASE.STASIS) {
            POSITION positionToRemove = getPositionFromPlayer(player);
            players.remove(positionToRemove);

            _playerSeats.remove(player);

            playersPocketCard.remove(player);
            _dealer.removeSubscriber(player);
            player.removeSubscriber(_dealer);

            // TODO: Move to PlayerSeatRegistry.Update()
            //_positionHandler.releaseUsed(positionToRemove);
        } else {
            //_waitingPlayers.add(new ImmutableKV<IPlayer, PLAYER_INTENT>(player, PLAYER_INTENT.LEAVE));
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", PLAYER_INTENT.LEAVE);
        jsonObject.put("player", player);
        publish(jsonObject);
    }
    public int getPlayerCount() { return _playerSeats.size(); }
    public int getMaxPlayerCount() { return _maxPlayerCount; }

    @Deprecated
    public IPlayer getPlayerFromPosition(POSITION positionName) {
        if (players.containsKey(positionName)) {
            return players.get(positionName);
        }

        return NullPlayer.GetSingleton();
    }

    //TODO: Call from PlayerSeatRegistry
    /*
    public IPlayer getPlayerFromSeat(PlayerSeat playerSeat) {

        for (Map.Entry<IPlayer, PlayerSeat> e: _playerSeats.entrySet()) {
            if (e.getValue().equals(playerSeat)) {
                return e.getKey();
            }
        }

        return NullPlayer.GetSingleton();

    }
     */

    @Deprecated
    public POSITION getPositionFromPlayer(IPlayer concretePlayer) {
        for (Map.Entry<POSITION, IPlayer> e: players.entrySet()) {
            if (e.getValue().equals(concretePlayer)) {
                return e.getKey();
            }
        }

        return POSITION.NONE;
    }

    /*
    public IPlayerSeat getSeatFromPlayer(IPlayer player) {
        if (_playerSeats.containsKey(player)) {
            return _playerSeats.get(player);
        }

        return (IPlayerSeat) NullPlayerSeat.GetSingleton();
    }
     */

    public void foldPosition(POSITION foldPositionName) {
        // TODO: Direct communication between PlayerSeatReg and Dealer
        //_positionHandler.releasePlaying(foldPositionName);
    }
    public Board getBoard() { return _board; }
    public Game getGame() { return _game; }
    public Pot getPot() { return _pot; }

    /**
     * Mandatory payment of blinds by players
     */
    public int blindBet() {
        IPlayer player_SB = getPlayerFromPosition(POSITION.SB);
        IPlayer player_BB = getPlayerFromPosition(POSITION.BB);

        int BB = _game.getBB();
        int SB = BB / 2;
        /* TODO: Implements banking system
        playersStack.put(player_SB, playersStack.get(player_SB) - SB);
        playersStack.put(player_BB,  playersStack.get(player_BB) - BB);
         */

        return BB + SB;

    }


    public IPlayer getPlayerFromSeatNumber(int seatNumber) {
        return _playerSeats.getPlayerFromSeatNumber(seatNumber);
    }

    /**
     * @param event
     */
    @Override
    public void update(Event event) {
        IEventType eventType = (IEventType) event.data.get("type");
        if(eventType == DEALER_INTENT.QUERY_ACTION) {
            try {
                wait(2000);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", EVENT.TIMEOUT);
                jsonObject.put("player", event.data.get("player"));
                publish(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (eventType == PLAYER_INTENT.JOIN || eventType == PLAYER_INTENT.LEAVE) {
            JSONObject jsonObject = event.data;
            jsonObject.put("player", event.source);
            publish(jsonObject);
        } else if (eventType == EVENT.POT_ADD) {
            _pot.add((int) event.data.get("value"));
            JSONObject jsonObject = event.data;
            publish(jsonObject);
        } else if (eventType == BettingRound.EVENT.NEXT) {
            if (_dealer == event.source) {
                // TODO: Dealer send BettingRound.EVENT.NEXT
                nextBettingRoundPhase();
            }
        }

    }

    @Override
    public void addSubscriber(ISubscriber subscriber) {
        _publisher.addSubscriber(subscriber);
    }

    @Override
    public void removeSubscriber(ISubscriber subscriber) {
        _publisher.removeSubscriber(subscriber);
    }

    @Override
    public void publish(JSONObject jsonObject) {
        _publisher.publish(jsonObject);
    }
}
