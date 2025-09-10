package org.quazarspirit.holdem4j.GameLogic.CardPile;

import org.quazarspirit.holdem4j.Card.Card;

public class PocketCards extends CardPile {
    static protected int maxSize = 2;

    @Override
    public void init() {
    }

    void fold(Deck deck) {
        DiscardPile dp = deck.getDiscardPile();
        for (Card c : cards) {
            cards.remove(c);
            dp.pushCard(c);
        }
    }
}
