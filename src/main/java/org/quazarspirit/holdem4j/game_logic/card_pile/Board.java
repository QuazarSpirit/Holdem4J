package org.quazarspirit.holdem4j.game_logic.card_pile;

import org.quazarspirit.holdem4j.game_logic.Card;

public class Board extends CardPile {
    static protected int _maxSize = 5;

    @Override
    public void init() {}

    @Override
    public int getMaxSize() { return Hand._maxSize; }
}
