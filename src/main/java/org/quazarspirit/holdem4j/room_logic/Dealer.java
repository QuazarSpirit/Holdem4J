package org.quazarspirit.holdem4j.room_logic;

import io.github.cdimascio.dotenv.Dotenv;
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
import org.quazarspirit.utils.publisher_subscriber_pattern.Event;
import org.quazarspirit.utils.publisher_subscriber_pattern.ISubscriber;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Class that shuffles and distributes cards
 * Draws board (flop, turn, river)
 * Handles hands
 * Handles bets, checks if blinds are paid
 * Handles rules in general
 * Computes pot size
 */
public class Dealer /*extends Thread*/ implements ISubscriber {
    private final URI MQ_Endpoint = URI.create("http://localhost:4000/message/add");
    private final Table _table;
    private Deck _deck;
    private Round.ROUND_PHASE _roundPhase;

    private final Producer _producer;
    private  boolean _started = false;

    Dealer(Table table) {
        _table = table;
        _deck = new Deck(table.getGame());
        _table.addSubscriber(this);
        _producer = new Producer();
    }

    public void run() {
        while (_started) {
            playRoundPhase();
        }
    }

    public void playRoundPhase() {
        _roundPhase = _table.getRound().getRoundPhase();
        System.out.println(_roundPhase.toString());

        if (_roundPhase == Round.ROUND_PHASE.STASIS) {
            return;
        }

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

        ArrayList<Position.NAME> usedPositions = _table.getPlayingPositions();
        ArrayList<KV<PLAYER_ACTION, Bet>> previousPlayerActions = new ArrayList<>();

        for (Iterator<Position.NAME> iterator = usedPositions.iterator(); iterator.hasNext();) {
            Position.NAME positionName = iterator.next();
            IPlayer player = _table.getPlayerFromPosition(positionName);

            ArrayList<PLAYER_ACTION> allowedActions = fillAllowedAction(previousPlayerActions);
            if (positionName == Position.NAME.BB && _roundPhase == Round.ROUND_PHASE.PRE_FLOP) {
                if (! allowedActions.contains(PLAYER_ACTION.CHECK)) {
                    allowedActions.add(PLAYER_ACTION.CHECK);
                }
            }

            KV<PLAYER_ACTION, Bet> pAction = player.queryAction(allowedActions, _table.getGame());
            previousPlayerActions.add(pAction);
            handlePlayerAction(pAction, allowedActions, player, iterator);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("round_phase", _roundPhase.toString());
        jsonObject.put("player_actions", previousPlayerActions);
        _producer.sendEvent(_roundPhase.toString(), MQ_Endpoint);
        _table.nextRoundPhase();
    }


    /**
     * Distribute pocket hands for each player
     */
    public void deal() {
        ArrayList<Position.NAME> playingPositions = _table.getPlayingPositions();
        HashMap<Position.NAME, PocketCards> pocketCardsList = new HashMap<>();

        System.out.println("----------- \nPlaying positions count: " + playingPositions.size());
        System.out.println("Playing positions: " + playingPositions);

        for (Position.NAME positionName: playingPositions) {
            pocketCardsList.put(positionName, new PocketCards());
        }

        for (int j = 0; j < 2; j+=1) {
            for (Position.NAME positionName: playingPositions) {
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
     * @param drawCount
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
     * @param playerActions Array of
     * @return Array of PLAYER_ACTION allowed from previous actions
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
     * @param pAction
     * @param allowedActions
     * @param player
     */
    void handlePlayerAction(KV<PLAYER_ACTION, Bet> pAction, ArrayList<PLAYER_ACTION> allowedActions, IPlayer player, Iterator<Position.NAME> iterator) {
        PLAYER_ACTION playerAction = pAction.getKey();
        if(allowedActions.contains(playerAction)) {
            switch (playerAction) {
                case FOLD -> {
                    ICardPile playerPCards = _table.getPocketCards(player);
                    _deck.discard(playerPCards);
                    iterator.remove();
                }
                case CALL -> {
                    // TODO: Call size depends on precedent actions
                    int callSize = _table.getGame().getBB();
                    _table.getPot().add(callSize);
                }
                case BET, RAISE -> {
                    // TODO: Proper RAISE implementation
                    _table.getPot().add(pAction.getValue().getSize());
                }
            }
        }
    }

    /**
     * @param event
     */
    @Override
    public void update(Event event) {
        if (event.data.get("type") == PLAYER_INTENT.JOIN) {
            if (_table.getPlayerCount() > 1 && ! _started) {

                if(!Utils.isTesting()) {
                    _started = true;
                    // this.start();
                }
            }
        } else if (event.data.get("type")  == PLAYER_INTENT.LEAVE) {
            if (_table.getPlayerCount() < 2) {
                _started = false;
                //this.interrupt();
            }
        }
    }
}
