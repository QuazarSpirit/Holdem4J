package org.quazarspirit.holdem4j.GameLogic.CardPile;

import org.quazarspirit.holdem4j.Card.Card;
import org.quazarspirit.holdem4j.PlayerLogic.Player.IPlayer;
import org.quazarspirit.holdem4j.RoomLogic.PositionEnum;
import org.quazarspirit.holdem4j.RoomLogic.PositionHandler;
import org.quazarspirit.holdem4j.RoomLogic.Table;

import java.util.ArrayList;

public class Hand extends CardPile {
    static protected final int _maxSize = 5;

    public enum HAND_RANK {
        NONE(-1), CARD_HIGH(0.9953015), PAIR(1.366477),
        DOUBLE_PAIR(20.03535), THREE_OF_A_KIND(46.32955),
        STRAIGHT(253.8), FLUSH(508.8019), FULL_HOUSE(693.1667),
        FOUR_OF_A_KIND(4164), STRAIGHT_FLUSH(72192.33), ROYAL_FLUSH(649739);

        private final double _probability;

        HAND_RANK(double probability) {
            _probability = probability;
        }

        public double getProbability() {
            return _probability;
        }
    }

    @Override
    public void init() {
    }

    public Hand() {
        super();
    }

    public Hand(Hand hand) {
        super();
        this.cards.clear();
        this.cards.addAll(hand.cards);
    }

    public Hand(CardPile cardPile, int fromIndex) {
        super();
        this.cards.clear();
        this.cards.addAll(cardPile.cards.subList(fromIndex, fromIndex + Hand._maxSize));
    }

    @Override
    public int getMaxSize() {
        return Hand._maxSize;
    }

    /**
     * Methods that cast cardPile to hand
     * 
     * @param board Board
     * @return hand
     */
    private Hand createHand(Board board) {
        Hand hand = new Hand();
        hand.cards.addAll(board.cards);
        return hand;
    }

    HAND_RANK computeRank(Table table, PositionEnum _name) {
        Board board = table.getBoard();
        IPlayer player = table.getPlayerFromPosition(_name);
        ICardPile cardPile = table.getPocketCards(player);

        if (cardPile.equals(NullCardPile.GetSingleton())) {
            return HAND_RANK.NONE;
        }

        PocketCards pocketCards = (PocketCards) cardPile;

        ArrayList<Card> combination = new ArrayList<Card>();
        // WARNING value 5 is for BettingRound:river
        // NOT WORKING FOR OMAHA
        /*
         * TODO: Implements
         * int boardSize =
         * BettingRound.ROUND_CARD_COUNT.get(table.getRound().getPhase());
         * 
         * // Compute board alone
         * HAND_RANK currentRank = RankEvaluator.evaluate(createHand(board)).getKey();
         * 
         * for(int i = 1; i < boardSize; i++) {
         * for (int j = i+1; j < boardSize; j++) {
         * Hand tmp_hand = createHand(board);
         * 
         * // Remplacement dans le board des cartes de la pocket card
         * for(int k=0; k < pocketCards.size(); k++) {
         * tmp_hand.cards.set(i, pocketCards.getCardAt(k));
         * }
         * 
         * RankEvaluator.evaluate(tmp_hand);
         * }
         * }
         */
        return HAND_RANK.NONE;
    }
}
