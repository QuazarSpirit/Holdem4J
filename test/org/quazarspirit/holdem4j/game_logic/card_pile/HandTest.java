package org.quazarspirit.holdem4j.game_logic.card_pile;

import org.junit.jupiter.api.Test;
import org.quazarspirit.holdem4j.TestLifecycle;
import org.quazarspirit.holdem4j.game_logic.Card;
import org.quazarspirit.utils.Utils;

import static org.junit.jupiter.api.Assertions.*;

class HandTest extends TestLifecycle {
    @Test
    void sortRank() {
        Hand hand = new Hand();
        Deck deck = new Deck().shuffle();
        for(int i = 0; i < hand.getMaxSize(); i++) {
            hand.pushCard(deck.getCardAt(i));
        }
        Utils.Log("Rank: " + hand.asString(CardPile.SORT_CRITERIA.RANK));

        hand.sort(CardPile.SORT_CRITERIA.RANK);
        Utils.Log(hand.asString(CardPile.SORT_CRITERIA.RANK) + "\n -------");

        assertTrue(hand.isSorted(CardPile.SORT_CRITERIA.RANK));
    }

    @Test
    void sortColor() {
        Hand hand = new Hand();
        Deck deck = new Deck().shuffle();
        for(int i = 0; i < hand.getMaxSize(); i++) {
            hand.pushCard(deck.getCardAt(i));
        }
        Utils.Log("Color: " + hand.asString(CardPile.SORT_CRITERIA.COLOR));

        hand.sort(CardPile.SORT_CRITERIA.COLOR);
        Utils.Log(hand.asString(CardPile.SORT_CRITERIA.COLOR) + "\n --------");

        assertTrue(hand.isSorted(CardPile.SORT_CRITERIA.COLOR));
    }
}