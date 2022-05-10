package org.quazarspirit.holdem4j.game_logic;

import org.junit.jupiter.api.Test;
import org.quazarspirit.holdem4j.game_logic.card_pile.CardPile;
import org.quazarspirit.holdem4j.game_logic.card_pile.Deck;
import org.quazarspirit.holdem4j.game_logic.card_pile.Hand;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RankEvaluatorTest {
    @Test
    void evaluateRoyalFlush() {
        Deck deck = new Deck();
        Hand hand = new Hand();


        for(int i = 8; i < deck.getMaxSize(); i+=13) {
            hand.clear();
            for (int j = 0; j < hand.getMaxSize(); j++) {
                hand.pushCard(deck.getCardAt(i + j));
            }
            Map.Entry<Hand.HAND_RANK, String> eval = RankEvaluator.evaluate(hand);

            /*
            System.out.println(hand.asString(CardPile.SORT_CRITERIA.VALUE));
            System.out.println(eval.getValue());
             */
            assertEquals(eval.getKey(), Hand.HAND_RANK.ROYAL_FLUSH);
            assertEquals(eval.getValue(), deck.getCardAt(i + hand.getMaxSize() -1).getRank());
        }
    }

    @Test
    void evaluateStraightFlush() {
        Deck deck = new Deck();
        Hand hand = new Hand();

        for (int k = 0; k < 8; k++) {
            for(int i = k; i < deck.getMaxSize(); i+=13) {
                hand.clear();
                for (int j = 0; j < hand.getMaxSize(); j++) {
                    hand.pushCard(deck.getCardAt(i + j));
                }
                Map.Entry<Hand.HAND_RANK, String> eval = RankEvaluator.evaluate(hand);
                /*
                System.out.println(hand.asString(CardPile.SORT_CRITERIA.VALUE));
                System.out.println(eval.getValue());
                 */
                assertEquals(eval.getKey(), Hand.HAND_RANK.STRAIGHT_FLUSH);
                assertEquals(eval.getValue(), deck.getCardAt(k + hand.getMaxSize() -1).getRank());
            }
        }
    }

    @Test
    void evaluateFlush() {
        Deck initialDeck = new Deck();
        Deck deck = initialDeck.shuffle();
        deck.sort(CardPile.SORT_CRITERIA.COLOR);
        System.out.println(deck.asString(CardPile.SORT_CRITERIA.VALUE));
        Hand hand = new Hand();

        for (int k = 0; k < 8; k++) {
            for(int i = k; i < deck.getMaxSize(); i+=13) {
                hand.clear();
                for (int j = 0; j < hand.getMaxSize(); j++) {
                    hand.pushCard(deck.getCardAt(i + j));
                }
                Map.Entry<Hand.HAND_RANK, String> eval = RankEvaluator.evaluate(hand);


                assertTrue((eval.getKey().equals(Hand.HAND_RANK.STRAIGHT_FLUSH)
                    || eval.getKey().equals(Hand.HAND_RANK.FLUSH)));

                hand.sort(CardPile.SORT_CRITERIA.RANK);
                assertEquals(eval.getValue(), hand.getCardAt(hand.getMaxSize() -1).getRank());
            }
        }
    }
}