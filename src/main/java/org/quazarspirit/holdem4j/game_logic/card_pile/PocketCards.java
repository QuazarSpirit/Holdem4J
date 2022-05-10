package org.quazarspirit.holdem4j.game_logic.card_pile;

import org.quazarspirit.holdem4j.game_logic.Card;

public class PocketCards extends CardPile{
    static protected int maxSize = 2;

    @Override
    public void init() {}



    void fold(Deck deck) {
        DiscardPile dp = deck.getDiscardPile();
        for (Card c: cards) {
            cards.remove(c);
            dp.pushCard(c);
        }
    }
}
