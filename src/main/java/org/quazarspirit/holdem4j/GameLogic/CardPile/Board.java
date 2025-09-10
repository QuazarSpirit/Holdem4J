package org.quazarspirit.holdem4j.GameLogic.CardPile;

import org.quazarspirit.holdem4j.Card.Card;

public class Board extends CardPile {
    static protected int _maxSize = 5;

    @Override
    public void init() {
    }

    @Override
    public int getMaxSize() {
        return Hand._maxSize;
    }
}
