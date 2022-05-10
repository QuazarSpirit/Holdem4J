package org.quazarspirit.holdem4j.game_logic;


import org.quazarspirit.holdem4j.game_logic.card_pile.CardPile;
import org.quazarspirit.holdem4j.game_logic.card_pile.ICardPile;

public class NullCard extends Card {
    static final private NullCard _singleton = new NullCard();
    private NullCard() {
        super("");
    }

    static public NullCard GetSingleton() {
        return _singleton;
    }
}
