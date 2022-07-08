package org.quazarspirit.holdem4j.game_logic.card_pile;

import org.quazarspirit.holdem4j.game_logic.Card;

public class DiscardPile extends CardPile {
    static final protected int maxSize = 52;

    @Override
    public void init() {

    }

    public boolean pushCard(Card card) {
        // Needs to be moved in rule
        if (size() > maxSize || contains(card)) {
            return false;
        }
        // IRL consistency query
        cards.add(0, card);
        return true;
    }

}
