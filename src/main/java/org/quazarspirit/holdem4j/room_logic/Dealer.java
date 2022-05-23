package org.quazarspirit.holdem4j.room_logic;

import org.quazarspirit.holdem4j.game_logic.Bet;
import org.quazarspirit.holdem4j.game_logic.Card;
import org.quazarspirit.holdem4j.game_logic.Round;
import org.quazarspirit.holdem4j.game_logic.card_pile.Deck;
import org.quazarspirit.holdem4j.game_logic.card_pile.PocketCards;
import org.quazarspirit.holdem4j.room_logic.player_logic.IPlayer;
import org.quazarspirit.holdem4j.room_logic.player_logic.PLAYER_ACTION;
import org.quazarspirit.utils.ImmutableKV;

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
public class Dealer {
    private final Table _table;

    private Deck _deck = new Deck();

    private Round.ROUND_PHASE _roundPhase;

    Dealer(Table table) {
        _table = table;
    }

    void run() {
        // TODO: Bet handling

        _roundPhase = _table.getRound().getRoundPhase();

        if (_roundPhase == Round.ROUND_PHASE.PRE_FLOP) {
            _deck = _deck.shuffle();
            deal();
        } else {
            draw(_roundPhase.getDrawCount());
        }

        ArrayList<PositionHandler.POSITION_NAME> usedPositions = _table.getUsedPositions();
        ArrayList<ImmutableKV<PLAYER_ACTION, Bet>> previousPlayerActions = new ArrayList<>();
        for (PositionHandler.POSITION_NAME positionName: usedPositions) {
            IPlayer player = _table.getPlayerFromPosition(positionName);
            ImmutableKV<PLAYER_ACTION, Bet> pAction = player.queryAction();
            previousPlayerActions.add(pAction);
            handlePlayerAction(pAction, previousPlayerActions);
        }
    }

    /**
     * Distribute pocket hands for each player
     */
    void deal() {
        ArrayList<PositionHandler.POSITION_NAME> usedPositions = _table.getUsedPositions();
        HashMap<PositionHandler.POSITION_NAME, PocketCards> pocketCardsList = new HashMap<>();
        for (PositionHandler.POSITION_NAME positionName: usedPositions) {
            pocketCardsList.put(positionName, new PocketCards());
        }

        for (int j = 0; j < 2; j+=1) {
            for (PositionHandler.POSITION_NAME positionName: usedPositions) {
                Card pickedCard = _deck.pick(0);
                PocketCards pCard = pocketCardsList.get(positionName);
                pCard.pushCard(pickedCard);
            }
        }

        for (Object positionO: pocketCardsList.keySet().toArray()) {
            PositionHandler.POSITION_NAME positionName = (PositionHandler.POSITION_NAME) positionO;
            IPlayer player = _table.getPlayerFromPosition(positionName);
            _table.setPlayerPocketCards(player, pocketCardsList.get(positionName));
        }
    }

    /**
     * Add picked card to board depending on draw count
     * @param drawCount
     */
    void draw(int drawCount) {
        _deck.burn();
        for(int i = 0; i < drawCount; i+=1) {
            Card pickedCard = _deck.pick(0);
            _table.getBoard().pushCard(pickedCard);
        }
    }

    /**
     * Compute allowedActions from previousPlayer actions for current Phase
     * @param playerActions
     * @return
     */
    ArrayList<PLAYER_ACTION> fillAllowedAction(ArrayList<ImmutableKV<PLAYER_ACTION, Bet>> playerActions) {
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

        ImmutableKV<PLAYER_ACTION, Bet> previousPlayerAction = null;
        for(int i = 0; i < playerActions.size(); i++) {
            ImmutableKV<PLAYER_ACTION, Bet> currentAction = playerActions.get(i);
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

    void handlePlayerAction(ImmutableKV<PLAYER_ACTION, Bet> pAction,  ArrayList<ImmutableKV<PLAYER_ACTION, Bet>> previousPlayerActions) {
        ArrayList<PLAYER_ACTION> allowedActions = fillAllowedAction(previousPlayerActions);

        PLAYER_ACTION playerAction = pAction.getKey();
        if(allowedActions.contains(playerAction)) {
            switch (playerAction) {
            }
        }
    }
}
