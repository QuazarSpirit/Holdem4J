package org.quazarspirit.holdem4j.room_logic;

import org.json.JSONArray;
import org.json.JSONObject;
import org.quazarspirit.holdem4j.game_logic.Bet;
import org.quazarspirit.holdem4j.game_logic.Card;
import org.quazarspirit.holdem4j.game_logic.Round;
import org.quazarspirit.holdem4j.game_logic.card_pile.Deck;
import org.quazarspirit.holdem4j.game_logic.card_pile.ICardPile;
import org.quazarspirit.holdem4j.game_logic.card_pile.PocketCards;
import org.quazarspirit.holdem4j.player_logic.IPlayer;
import org.quazarspirit.holdem4j.player_logic.PLAYER_ACTION;
import org.quazarspirit.holdem4j.player_logic.PLAYER_INTENT;
import org.quazarspirit.utils.KV;
import org.quazarspirit.utils.Utils;
import org.quazarspirit.utils.message_queue_pattern.Producer;
import org.quazarspirit.utils.publisher_subscriber_pattern.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class that shuffles and distributes cards
 * Draws board (flop, turn, river)
 * Handles hands
 * Handles bets, checks if blinds are paid
 * Handles rules in general
 * Computes pot size
 */
public class Dealer extends Thread implements ISubscriber, IPublisher {
    private final URI MQ_Endpoint = URI.create("http://localhost:4000/message/add");
    private final Table _table;
    private final Producer _producer = new Producer();
    private final Publisher _publisher = new Publisher(this);
    private Round.ROUND_PHASE _roundPhase;

    private ArrayList<Position.NAME> _currPlayingPos = new ArrayList<>();
    private ArrayList<KV<PLAYER_ACTION, Bet>> _previousPlayerActions = new ArrayList<>();
    private boolean _started = false;
    private Deck _deck;

    Dealer(Table table) {
        _table = table;
        _table.addSubscriber(this);
        _deck = new Deck(table.getGame());
    }

    public synchronized void run() {
        while (_started) {
            playRoundPhase();
        }
    }

    public synchronized void playRoundPhase() {
        _roundPhase = _table.getRound().getRoundPhase();

        if (_roundPhase == Round.ROUND_PHASE.STASIS) { return; }

        System.out.println(_roundPhase.toString());

        if (_roundPhase == Round.ROUND_PHASE.PRE_FLOP) {
            _deck = new Deck(_table.getGame());
            if(!Utils.isTesting()) {
                _deck = _deck.shuffle();
                System.out.println("New deck: " + _deck.asString());
            }

            _table.getPot().add(_table.blindBet());
            deal();
        } else {
            draw(_roundPhase.getDrawCount());
        }

        _currPlayingPos.clear();
        _previousPlayerActions.clear();
        _currPlayingPos = _table.getPlayingPositions();
        sendQueryActionEvent(_currPlayingPos.get(0), new ArrayList<>());

        /*
        JSONObject mqObject = new JSONObject();
        mqObject.put("round_phase", _roundPhase.toString());
        _producer.sendEvent(_roundPhase.toString(), MQ_Endpoint);
         */
    }

    void sendQueryActionEvent(Position.NAME posName,  ArrayList<KV<PLAYER_ACTION, Bet>> previousActions) {
        ArrayList<PLAYER_ACTION> allowedActions = fillAllowedAction(previousActions);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", DEALER_INTENT.QUERY_ACTION);
        jsonObject.put("player", _table.getPlayerFromPosition(posName));
        jsonObject.put("table", _table);
        jsonObject.put("allowed_actions", allowedActions);
        publish(jsonObject);
    }

    void onPlayerAction(Event event) {
        KV<PLAYER_ACTION, Bet> pAction;
        IPlayer player;

        pAction = new KV<>(PLAYER_ACTION.FOLD, new Bet(_table.getGame()));
        if (event.data.get("type") == Table.EVENT.TIMEOUT) {
            player = (IPlayer) event.data.get("player");
        } else {
            player = (IPlayer) event.source;
            try {
                JSONArray jsonArray = (JSONArray) event.data.get("player_action");
                System.out.println(jsonArray.get(0).getClass().getName());


                pAction = (KV<PLAYER_ACTION, Bet>) event.data.get("player_action");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        _previousPlayerActions.add(pAction);
        ArrayList<PLAYER_ACTION> allowedActions = fillAllowedAction(_previousPlayerActions);
        handlePlayerAction(pAction, allowedActions, player);

        Position.NAME nextPos = Position.NAME.NONE;
        for(int i = 0; i < _currPlayingPos.size(); i += 1) {
            Position.NAME posName = _currPlayingPos.get(i);
            if (posName == _table.getPositionFromPlayer(player)) {
                nextPos = _currPlayingPos.get(i + 1);
                break;
            }
        }

        if (nextPos != Position.NAME.NONE) {
            sendQueryActionEvent(nextPos, _previousPlayerActions);
        } else {
            _table.nextRoundPhase();
        }
    }


    /**
     * Distribute pocket hands for each player
     */
    public synchronized void deal() {
        ArrayList<Position.NAME> playingPositions = _table.getPlayingPositions();
        HashMap<Position.NAME, PocketCards> pocketCardsList = new HashMap<>();

        System.out.println("----------- \nPlaying positions count: " + playingPositions.size());
        System.out.println("Playing positions: " + playingPositions);

        for (Position.NAME positionName : playingPositions) {
            pocketCardsList.put(positionName, new PocketCards());
        }

        for (int j = 0; j < 2; j+=1) {
            for (Position.NAME positionName : playingPositions) {
                Card pickedCard = _deck.pick(0);
                PocketCards pCard = pocketCardsList.get(positionName);
                pCard.pushCard(pickedCard);
            }
        }

        for (Object positionO: pocketCardsList.keySet().toArray()) {
            Position.NAME positionName = (Position.NAME) positionO;
            IPlayer player = _table.getPlayerFromPosition(positionName);
            _table.setPlayerPocketCards(player, pocketCardsList.get(positionName));
        }
    }

    /**
     * Add picked card to board depending on draw count
     * @param drawCount Number of card to be added to board (usually 1 or 3)
     */
    public void draw(int drawCount) {
        if (drawCount < 1) { return; }

        _deck.burn();
        for(int i = 0; i < drawCount; i+=1) {
            Card pickedCard = _deck.pick(0);
            _table.getBoard().pushCard(pickedCard);
        }
    }

    /**
     * Compute allowedActions from previousPlayer actions for current Phase
     * @param playerActions Array of previous player actions
     * @return Array of allowed PLAYER_ACTION
     */
    ArrayList<PLAYER_ACTION> fillAllowedAction(ArrayList<KV<PLAYER_ACTION, Bet>> playerActions) {
        ArrayList<PLAYER_ACTION> allowedActions = new ArrayList<>() {
            {
                add(PLAYER_ACTION.FOLD);
                add(PLAYER_ACTION.CALL);
                add(PLAYER_ACTION.BET);
            }
        };

        if (_roundPhase != Round.ROUND_PHASE.PRE_FLOP) {
            allowedActions.add(PLAYER_ACTION.CHECK);
        }

        KV<PLAYER_ACTION, Bet> previousPlayerAction = new KV<PLAYER_ACTION, Bet>();
        for(int i = 0; i < playerActions.size(); i++) {
            KV<PLAYER_ACTION, Bet> currentAction = playerActions.get(i);
            if (i > 0) {
                switch (previousPlayerAction.getKey()) {
                    case CALL -> allowedActions.add(PLAYER_ACTION.BET);
                    case BET -> {
                        allowedActions.remove(PLAYER_ACTION.CHECK);
                        allowedActions.remove(PLAYER_ACTION.BET);
                    }
                }
            }

            previousPlayerAction = currentAction;
        }

        return allowedActions;
    }

    /**
     * Executes action that player selected
     * @param pAction Action that player selected
     * @param allowedActions List of actions that player could have selected
     * @param player Player object
     */
    synchronized void handlePlayerAction(KV<PLAYER_ACTION, Bet> pAction, ArrayList<PLAYER_ACTION> allowedActions, IPlayer player) {
        PLAYER_ACTION playerAction = pAction.getKey();
        if(allowedActions.contains(playerAction)) {
            switch (playerAction) {
                case FOLD -> {
                    ICardPile playerPCards = _table.getPocketCards(player);
                    _deck.discard(playerPCards);
                    // Call fold position that adds position to _waitingRelease
                    _table.foldPosition(_table.getPositionFromPlayer(player));
                }
                case CALL -> {
                    // TODO: Call size depends on precedent actions
                    int callSize = _table.getGame().getBB();
                    _table.getPot().add(callSize);
                }
                case BET, RAISE -> // TODO: Proper RAISE implementation
                        _table.getPot().add(pAction.getValue().getSize());
            }
        }
    }

    /**
     * @param event Event sent by Publisher
     */
    @Override
    public void update(Event event) {
        IEventType eventType = (IEventType) event.data.get("type");

        if (eventType == PLAYER_INTENT.JOIN) {
            if (_table.getPlayerCount() > 1 && ! _started) {
                if(!Utils.isTesting()) {
                    _started = true;
                    //this.run();
                    this.start();
                }
            }
        }
        else if (eventType  == PLAYER_INTENT.LEAVE) {
            if (_table.getPlayerCount() < 2) {
                _started = false;
            }
        }
        else if (eventType == Round.EVENT.NEXT) {
            if(_table.getRound().getRoundPhase() == Round.ROUND_PHASE.STASIS) {
                _started = false;
                this.interrupt();
            }
            else {
                if (!_started) {
                    _started = true;
                    this.start();
                }
            }
        }
        else if (eventType == PLAYER_INTENT.ACT || eventType == Table.EVENT.TIMEOUT) {
            onPlayerAction(event);
        }
    }

    /**
     * @param subscriber
     */
    @Override
    public void addSubscriber(ISubscriber subscriber) {
        _publisher.addSubscriber(subscriber);
    }

    /**
     * @param subscriber
     */
    @Override
    public void removeSubscriber(ISubscriber subscriber) {
        _publisher.removeSubscriber(subscriber);
    }

    /**
     * @param jsonObject
     */
    @Override
    public void publish(JSONObject jsonObject) {
        _publisher.publish(jsonObject);
    }
}
