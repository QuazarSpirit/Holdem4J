package org.quazarspirit.holdem4j.game_logic.card_pile;

import org.quazarspirit.holdem4j.game_logic.Card;

import java.util.ArrayList;

public abstract class CardPile {
    static protected int maxSize = 52;
    final protected ArrayList<Card> cards = new ArrayList<Card>();
    protected boolean contains(Card cardToCheck) {
        for (Card card: cards) {
            if (card.getValue().equals(cardToCheck.getValue())) {
                return true;
            }
        }
        return false;
    }
    public int size() {
        return cards.size();
    }
    public boolean isEmpty() {
        return cards.size() == 0;
    }
    abstract protected void init();
    public boolean pushCard(Card card) {
        // Needs to be moved in rule
        if (size() > maxSize || contains(card)) {
            return false;
        }
        cards.add(card);
        return true;
    }
}
