package org.quazarspirit.holdem4j.GameLogic.ChipPile;

import org.quazarspirit.holdem4j.GameLogic.Game;

/**
 * Chip unit express in cents of money
 * 100 Chip = 1 SB
 * NL 1 ¤ -> 1 Chip = 0.5 ¤
 *
 */
public class Chip {
    final int _value;

    public Chip(Game game) {
        // Registering SB ( dividing by 2 multiplying by 100 )
        _value = game.getBB() * 50;
    }

    /**
     * @return Return SB from Chip
     */
    public float getValue() {
        return (float) _value / 100;
    }

    public String asString() {
        return " " + getValue();
    }
}
