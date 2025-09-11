package org.quazarspirit.holdem4j.CardPile;

import org.quazarspirit.holdem4j.Card;

public class PocketCards extends CardPile {
    public PocketCards(int deckMaxSize) {
        super(deckMaxSize);
    }

    void fold(Deck deck) {
        DiscardPile dp = deck.getDiscardPile();
        for (Card c : cards) {
            cards.remove(c);
            dp.pushCard(c);
        }
    }
}
