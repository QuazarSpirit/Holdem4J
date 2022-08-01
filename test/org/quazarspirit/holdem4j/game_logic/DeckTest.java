package org.quazarspirit.holdem4j.game_logic;

import org.junit.jupiter.api.Test;
import org.quazarspirit.holdem4j.TestLifecycle;
import org.quazarspirit.holdem4j.game_logic.card_pile.CardPile;
import org.quazarspirit.holdem4j.game_logic.card_pile.Deck;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest extends TestLifecycle {

    Pattern compileRegEx(String cardRange) {
        String regEx = "^(" + cardRange + "){4}$";
        return Pattern.compile(regEx);
    }
    @Test
    void constructor() {
        Deck deck = new Deck();
        assertEquals(deck.getMaxSize(), deck.size());
    }

    @Test
    void initWithoutArgs() {
        Pattern pattern = compileRegEx(Card.RANKS);

        Deck deck = new Deck();
        assertEquals(deck.getMaxSize(), deck.size());
        Matcher match = pattern.matcher(deck.asString(CardPile.SORT_CRITERIA.RANK, ""));
        assertTrue(match.matches());
    }

    @Test
    void initWithCardRange() {
        String cardRange = "2345A";
        Pattern pattern = compileRegEx(cardRange);

        Deck deck = new Deck();
        deck.init(cardRange);
        assertEquals(deck.size(), cardRange.length() * Card.COLORS.length());
        Matcher match = pattern.matcher(deck.asString(CardPile.SORT_CRITERIA.RANK, ""));
        assertTrue(match.matches());
    }

    @Test
    void shuffle() {
        Deck deck = new Deck();
        Deck shuffledDeck = deck.shuffle();
        assertEquals(shuffledDeck.size(),52);
    }

    @Test
    void asString() {
        Deck deck = new Deck();
        final String defaultDeckValidator =
            "2c/3c/4c/5c/6c/7c/8c/9c/Tc/Jc/Qc/Kc/Ac/2d/3d/4d/5d/6d/7d/8d/9d/Td/Jd/Qd/Kd/Ad/" +
            "2h/3h/4h/5h/6h/7h/8h/9h/Th/Jh/Qh/Kh/Ah/2s/3s/4s/5s/6s/7s/8s/9s/Ts/Js/Qs/Ks/As";

        String deckAsString = deck.asString(CardPile.SORT_CRITERIA.VALUE);
        assertEquals(deckAsString, defaultDeckValidator);
    }

    @Test
    void pick() {
        Deck deck = new Deck();
        Card pickedCard = deck.pick(0);
        assertTrue(pickedCard.isValid());
        assertEquals(pickedCard.getValue(), "2c");
        assertEquals(deck.size(), 51);
    }

    @Test
    void clear() {
        Deck deck = new Deck();
        deck.clear();
        assertEquals(deck.size(), 0);
    }

    @Test
    void pushCard() {
        Deck deck = new Deck();
        deck.clear();

        boolean pushResult = deck.pushCard(new Card("2c"));
        assertEquals(deck.size(), 1);
        assertTrue(pushResult);

        pushResult = deck.pushCard(new Card("2c"));
        assertFalse(pushResult);
    }

    @Test
    void pushCardWithFullDeck() {
        Deck deck = new Deck();

        boolean pushResult = deck.pushCard(new Card("2c"));
        assertFalse(pushResult);
    }

    @Test
    void getCardAt() {
        Deck deck = new Deck();
        Card card = deck.getCardAt(51);

        assertEquals(card.getValue(), "As");
    }

    @Test
    void size() {
        Deck deck = new Deck();
        assertEquals(deck.size(), 52);


        deck.pick(51);
        assertEquals(deck.size(), 51);
    }

    @Test
    void isEmpty() {
        Deck deck = new Deck();
        assertFalse(deck.isEmpty());

        deck.clear();
        assertTrue(deck.isEmpty());
    }
}