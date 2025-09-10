package org.quazarspirit.holdem4j.CardPile;

import org.quazarspirit.holdem4j.Card;

public class DiscardPile extends CardPile {
    DiscardPile(int deckMaxSize) {
        super(deckMaxSize);
    }

    static final protected int maxSize = 52;

    public boolean pushCard(Card card) {
        // Needs to be moved in rule
        if (getSize() > maxSize || contains(card)) {
            return false;
        }
        // IRL consistency query
        cards.add(0, card);
        return true;
    }

}
