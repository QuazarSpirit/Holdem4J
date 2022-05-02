package org.quazarspirit.holdem4j.game_logic.card_pile;

import org.quazarspirit.holdem4j.game_logic.Card;
import org.quazarspirit.holdem4j.game_logic.Round;
import org.quazarspirit.holdem4j.room_logic.PositionHandler;
import org.quazarspirit.holdem4j.room_logic.Table;
import org.quazarspirit.holdem4j.room_logic.player_logic.IPlayer;

import java.util.ArrayList;

public class Hand extends CardPile {
    static protected int maxSize = 5;

    enum HAND_RANK {
        NONE, CARD_HIGH, PAIR, DOUBLE_PAIR, THREE_OF_A_KIND,
        STRAIGHT, FLUSH, FULL, FOUR_OF_A_KIND, STRAIGHT_FLUSH, ROYAL_FLUSH
    }
    @Override
    protected void init() {}

    HAND_RANK computeRank(Table table, PositionHandler.POSITION_NAME position_name) {
        Board board = table.getBoard();
        IPlayer player = table.getPlayerFromPosition(position_name);

        ArrayList<Card> combination = new ArrayList<Card>();
        // WARNING value 5 is for Round:river
        int boardSize = Round.ROUND_CARD_COUNT.get(table.getRound().getRoundState());
        for(int i = 1; i < boardSize; i++) {
            for (int j = i+1; j < boardSize; j++) {
                Board tmp_board = new Board();
                tmp_board.cards.addAll(board.cards);
                // TODO: Implements
                /*
                    tmp_board.cards.set(i, )
                    tmp_board.cards.set(j)
                 */
            }
        }

        return HAND_RANK.NONE;
    }
}
