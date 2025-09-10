package org.quazarspirit.holdem4j.RoomLogic;

import org.json.JSONObject;
import org.quazarspirit.Utils.ImmutableKV;
import org.quazarspirit.Utils.Utils;
import org.quazarspirit.Utils.MessageQueue.Producer;
import org.quazarspirit.Utils.PubSub.*;
import org.quazarspirit.holdem4j.Card;
import org.quazarspirit.holdem4j.CardPile.Board;
import org.quazarspirit.holdem4j.CardPile.Deck;
import org.quazarspirit.holdem4j.CardPile.ICardPile;
import org.quazarspirit.holdem4j.CardPile.PocketCards;
import org.quazarspirit.holdem4j.GameLogic.BettingRound;
import org.quazarspirit.holdem4j.GameLogic.ChipPile.Bet;
import org.quazarspirit.holdem4j.GameLogic.ChipPile.IBet;
import org.quazarspirit.holdem4j.GameLogic.ChipPile.NullBet;
import org.quazarspirit.holdem4j.PlayerLogic.PlayerActionEnum;
import org.quazarspirit.holdem4j.PlayerLogic.PlayerIntentEnum;
import org.quazarspirit.holdem4j.PlayerLogic.Player.IPlayer;

import java.net.URI;
import java.util.*;

/**
 * Class that shuffles and distributes cards.<br>
 * Draws board (flop, turn, river).<br>
 * Handle hands.<br>
 * Handle bets, checks if blinds are paid.<br>
 * Handle rules in general.<br>
 * Computes pot size.
 */
public class Dealer /* extends Thread */ implements ISubscriber, IPublisher {
    private final URI MQ_Endpoint = URI.create("http://localhost:4000/message/add");
    private Deck _deck;
    private final Table _table;

    private final Producer _producer = new Producer();
    private final Publisher _publisher = new Publisher(this);
    private BettingRound.PHASE _roundPhase;
    private ArrayList<PositionEnum> _currPlayingPos = new ArrayList<>();

    private final ArrayList<ImmutableKV<PlayerActionEnum, Bet>> _previousPlayerActions = new ArrayList<>();

    Dealer(Table table) {
        _table = table;
        _table.addSubscriber(this);
        _deck = new Deck(table.getGame());
    }

    public boolean canStart() {
        return _table.getGame().getFormat().canStart(_table);
    }

    public void playRoundPhase() {
        if (!canStart() && !Utils.IsTesting()) {
            Utils.Log("Dealer can't start");
            return;
        }
        _roundPhase = _table.getRound().getPhase();
        // Utils.Log(_roundPhase.toString());

        switch (_roundPhase) {
            case STASIS -> {
                return;
            }
            case PRE_FLOP -> {
                _deck = new Deck(_table.getGame());
                if (!Utils.IsTesting()) {
                    _deck = _deck.shuffle();
                    Utils.Log(
                            "New deck: " + _deck.asString(),
                            new ImmutableKV<String, Object>("type", "GAME"));
                }

                _table.getPot().add(_table.blindBet());
                deal();
            }
            default -> {
                draw(_roundPhase.getDrawCount());
            }
        }

        // Resetting arrays for new round phase
        _currPlayingPos.clear();
        _previousPlayerActions.clear();

        _currPlayingPos = _table.getPlayingPositions();

        // Send query action event to first player
        if (!Utils.IsTesting()) {
            sendQueryActionEvent(_currPlayingPos.get(0), _previousPlayerActions);
        }

        /*
         * JSONObject mqObject = new JSONObject();
         * mqObject.put("round_phase", _roundPhase.toString());
         * _producer.sendEvent(_roundPhase.toString(), MQ_Endpoint);
         */
    }

    public List<PlayerActionEnum> getKeys(ArrayList<ImmutableKV<PlayerActionEnum, Bet>> a) {
        return a.stream().map(AbstractMap.SimpleImmutableEntry::getKey).toList();
    }

    public void sendQueryActionEvent(PositionEnum posName,
            ArrayList<ImmutableKV<PlayerActionEnum, Bet>> previousActions) {
        ArrayList<PlayerActionEnum> allowedActions = fillAllowedAction(getKeys(previousActions));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", DealerIntentEnum.QUERY_ACTION);
        jsonObject.put("player", _table.getPlayerFromPosition(posName));
        jsonObject.put("allowed_actions", allowedActions);
        jsonObject.put("table", _table);
        publish(jsonObject);
    }

    private void onPlayerAction(Event event) {
        JSONObject eventData = event.data;
        IPlayer player;

        Bet playerBet;
        try {
            playerBet = (Bet) eventData.get("bet");
        } catch (Exception e) {
            playerBet = null;
        }

        PlayerActionEnum playerAction = PlayerActionEnum.FOLD;
        if (eventData.get("type") == Table.EVENT.TIMEOUT) {
            player = (IPlayer) eventData.get("player");
        } else {
            player = (IPlayer) event.source;
            try {
                playerAction = (PlayerActionEnum) eventData.get("player_action");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        _previousPlayerActions.add(new ImmutableKV<>(playerAction, playerBet));
        handlePlayerAction(eventData, player);

        PositionEnum nextPos = PositionEnum.NONE;
        for (int i = 0; i < _currPlayingPos.size(); i += 1) {
            PositionEnum posName = _currPlayingPos.get(i);
            if (posName == _table.getPositionFromPlayer(player)) {
                nextPos = _currPlayingPos.get(i + 1);
                break;
            }
        }

        if (nextPos != PositionEnum.NONE) {
            sendQueryActionEvent(nextPos, _previousPlayerActions);
        } else {
            // TODO: Sent event to TABLE
            _table.nextBettingRoundPhase();
        }
    }

    /**
     * Executes action that player selected
     * 
     * @param pAction Action that player selected
     * @param player  Player object
     */
    void handlePlayerAction(JSONObject pAction, IPlayer player) {
        PlayerActionEnum playerAction = (PlayerActionEnum) pAction.get("player_action");

        ArrayList<PlayerActionEnum> allowedActions = fillAllowedAction(getKeys(_previousPlayerActions));

        IBet bet = NullBet.getSingleton();
        try {
            bet = (IBet) pAction.get("bet");
        } catch (Exception e) {
            e.printStackTrace();
            // Means there is no bet
        }

        if (allowedActions.contains(playerAction)) {
            switch (playerAction) {
                case FOLD -> {
                    ICardPile playerPCards = _table.getPocketCards(player);
                    _deck.discard(playerPCards);
                    // Call fold position that adds position to _waitingRelease
                    // TODO: Send fold event to TABLE
                    _table.foldPosition(_table.getPositionFromPlayer(player));
                }
                case CALL -> {
                    // TODO: Call size depends on precedent actions and round phase

                    Optional<ImmutableKV<PlayerActionEnum, Bet>> highestBet = _previousPlayerActions.stream()
                            .reduce((pAct_1, pAct_2) -> pAct_1.getValue().get() > pAct_2.getValue().get() ? pAct_1
                                    : pAct_2);

                    if (highestBet.isPresent()) {
                        int callSize = highestBet.get().getValue().get();
                        sendPotAddEvent(callSize);
                    }
                    // _table.getPot().add(callSize);

                }
                case BET, RAISE -> {// TODO: Proper RAISE implementation
                    // _table.getPot().add(bet.get());
                    sendPotAddEvent(bet.get());
                }
            }
        }
    }

    private void sendPotAddEvent(int value) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", Table.EVENT.POT_ADD);
        jsonObject.put("value", value);
        publish(jsonObject);
    }

    /**
     * Distribute pocket hands for each player
     */
    public void deal() {
        ArrayList<PositionEnum> playingPositions = _table.getPlayingPositions();
        HashMap<PositionEnum, PocketCards> pocketCardsList = new HashMap<>();

        Utils.Log(
                "----------- \nPlaying positions count: " + playingPositions.size(),
                new ImmutableKV<String, Object>("message_type", "GAME"));

        Utils.Log(
                "Playing positions: " + playingPositions,
                new ImmutableKV<String, Object>("message_type", "GAME"));

        for (PositionEnum positionName : playingPositions) {
            pocketCardsList.put(positionName, new PocketCards());
        }

        for (int j = 0; j < 2; j += 1) {
            for (PositionEnum positionName : playingPositions) {
                Card pickedCard = _deck.pick(0);
                PocketCards pCard = pocketCardsList.get(positionName);
                pCard.pushCard(pickedCard);
            }
        }

        for (Object positionO : pocketCardsList.keySet().toArray()) {
            PositionEnum positionName = (PositionEnum) positionO;
            IPlayer player = _table.getPlayerFromPosition(positionName);
            // TODO: Send EVENT to TABLE
            _table.setPlayerPocketCards(player, pocketCardsList.get(positionName));
        }
    }

    public void drawWithContext(int drawCount, Deck deck, Board board) {
        if (drawCount < 1) {
            return;
        }

        deck.burn();
        for (int i = 0; i < drawCount; i += 1) {
            Card pickedCard = deck.pick(0);
            board.pushCard(pickedCard);
        }
    }

    /**
     * Add picked card to board depending on draw count
     * 
     * @param drawCount Number of card to be added to board (usually 1 or 3)
     */
    public void draw(int drawCount) {
        Board tmpBoard = new Board();
        drawWithContext(drawCount, _deck, tmpBoard);
        // TODO: Send EVENT to TABLE
        _table.getBoard().pushCard(tmpBoard);
    }

    /**
     * Compute allowedActions from previousPlayer actions for current Phase
     * 
     * @param playerActions Array of previous player actions
     * @return Array of allowed PLAYER_ACTION
     */
    ArrayList<PlayerActionEnum> fillAllowedAction(List<PlayerActionEnum> playerActions) {
        // Maybe refactor with RESTRAIN ABLE actions instead of getting every previous
        // actions
        ArrayList<PlayerActionEnum> allowedActions = new ArrayList<>() {
            {
                add(PlayerActionEnum.FOLD);
                add(PlayerActionEnum.CHECK);
                add(PlayerActionEnum.BET);
            }
        };

        // Inverting actions for correct order last in first out
        // If first element in this order is aggressive then check is transformed into
        // call
        Collections.reverse(playerActions);

        for (PlayerActionEnum currentAction : playerActions) {
            if (currentAction.isAggressive()) {
                // If move is aggressive then check is not possible anymore
                allowedActions.remove(PlayerActionEnum.CHECK);
                allowedActions.add(PlayerActionEnum.CALL);

                // If you bet or raise, you can't bet after, you need to either call, fold or
                // raise
                allowedActions.remove(PlayerActionEnum.BET);
                allowedActions.add(PlayerActionEnum.RAISE);

                // If move is aggressive you don't have to go further,
                // last aggressive move is the only thing meaningful
                break;
            }
        }

        return allowedActions;
    }

    /**
     * @param event Event sent by Publisher
     */
    @Override
    public void update(Event event) {
        IEventType eventType = (IEventType) event.data.get("type");

        if (eventType == PlayerIntentEnum.JOIN) {
            if (canStart()) {
                if (!Utils.IsTesting()) {
                    if (_table.getRound().getPhase() == BettingRound.PHASE.STASIS) {
                        // TODO: SEND EVENT TO TABLE
                        _table.nextBettingRoundPhase();
                    }
                }
            }
        } else if (eventType == PlayerIntentEnum.LEAVE) {
            if (!canStart()) {
                // TODO: Stop table depending on game variant
            }
        } else if (eventType == BettingRound.Event.NEXT) {
            if (_table.getRound().getPhase() != BettingRound.PHASE.STASIS) {
                if (!Utils.IsTesting()) {
                    playRoundPhase();
                }
            }
        } else if (eventType == PlayerIntentEnum.ACT || eventType == Table.EVENT.TIMEOUT) {
            onPlayerAction(event);
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
