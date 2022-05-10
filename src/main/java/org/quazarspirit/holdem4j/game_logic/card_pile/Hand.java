package org.quazarspirit.holdem4j.game_logic.card_pile;

import org.quazarspirit.holdem4j.game_logic.Card;
import org.quazarspirit.holdem4j.game_logic.RankEvaluator;
import org.quazarspirit.holdem4j.game_logic.Round;
import org.quazarspirit.holdem4j.room_logic.Table;
import org.quazarspirit.holdem4j.room_logic.PositionHandler;
import org.quazarspirit.holdem4j.room_logic.player_logic.IPlayer;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hand extends CardPile {
    static protected int _maxSize = 5;
    public enum HAND_RANK {
        NONE, CARD_HIGH, PAIR, DOUBLE_PAIR, THREE_OF_A_KIND,
        STRAIGHT, FLUSH, FULL_HOUSE, FOUR_OF_A_KIND, STRAIGHT_FLUSH, ROYAL_FLUSH
    }
    @Override
    public void init() {}

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
    public int getMaxSize() { return Hand._maxSize; }

    // Reg ex abandoned because group names can't be extracted without a lot of efforts
    public HAND_RANK computeRank(Hand hand) {
        String handAsString = hand.asString(SORT_CRITERIA.VALUE)
            .toUpperCase().replace(CARD_CHAR_SEPARATOR, " ");
        System.out.println(handAsString);


        return HAND_RANK.NONE;
    }

    /**
     * Methods that cast cardPile to hand
     * @param board Board
     * @return hand
     */
    private Hand createHand (Board board) {
        Hand hand = new Hand();
        hand.cards.addAll(board.cards);
        return hand;
    }

    HAND_RANK computeRank(Table table, PositionHandler.POSITION_NAME position_name) {
        Board board = table.getBoard();
        IPlayer player = table.getPlayerFromPosition(position_name);
        ICardPile cardPile = table.getPocketCards(player);

        if (cardPile.equals(NullCardPile.GetSingleton())) {
            return HAND_RANK.NONE;
        }

        PocketCards pocketCards = (PocketCards) cardPile;

        ArrayList<Card> combination = new ArrayList<Card>();
        // WARNING value 5 is for Round:river
        // NOT WORKING FOR OMAHA
        int boardSize = Round.ROUND_CARD_COUNT.get(table.getRound().getRoundState());

        // Compute board alone
        HAND_RANK currentRank = RankEvaluator.evaluate(createHand(board)).getKey();

        for(int i = 1; i < boardSize; i++) {
            for (int j = i+1; j < boardSize; j++) {
                Hand tmp_hand = createHand(board);

                // Remplacement dans le board des cartes de la pocket card
                for(int k=0; k < pocketCards.size(); k++) {
                    tmp_hand.cards.set(i, pocketCards.getCardAt(k));
                }

                RankEvaluator.evaluate(tmp_hand);
            }
        }

        return HAND_RANK.NONE;
    }
}
