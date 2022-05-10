package org.quazarspirit.holdem4j.game_logic.card_pile;

import org.junit.jupiter.api.Test;
import org.quazarspirit.holdem4j.game_logic.Card;

import static org.junit.jupiter.api.Assertions.*;

class HandTest {

    @Test
    void computeRank() {
        Hand hand = new Hand();
        Deck deck = new Deck(); //.shuffle();
        for(int i = 0; i < hand.getMaxSize(); i++) {
            hand.pushCard(deck.getCardAt(i));
        }
        System.out.println(hand.asString(CardPile.SORT_CRITERIA.VALUE));

        hand.computeRank(hand);

    }


    @Test
    void sortRank() {
        Hand hand = new Hand();
        Deck deck = new Deck().shuffle();
        for(int i = 0; i < hand.getMaxSize(); i++) {
            hand.pushCard(deck.getCardAt(i));
        }
        System.out.println("Rank: " + hand.asString(CardPile.SORT_CRITERIA.RANK));

        hand.sort(CardPile.SORT_CRITERIA.RANK);
        System.out.println(hand.asString(CardPile.SORT_CRITERIA.RANK) + "\n -------");

        assertTrue(hand.isSorted(CardPile.SORT_CRITERIA.RANK));
    }

    @Test
    void sortColor() {
        Hand hand = new Hand();
        Deck deck = new Deck().shuffle();
        for(int i = 0; i < hand.getMaxSize(); i++) {
            hand.pushCard(deck.getCardAt(i));
        }
        System.out.println("Color: " + hand.asString(CardPile.SORT_CRITERIA.COLOR));

        hand.sort(CardPile.SORT_CRITERIA.COLOR);
        System.out.println(hand.asString(CardPile.SORT_CRITERIA.COLOR) + "\n --------");

        assertTrue(hand.isSorted(CardPile.SORT_CRITERIA.COLOR));
    }
}