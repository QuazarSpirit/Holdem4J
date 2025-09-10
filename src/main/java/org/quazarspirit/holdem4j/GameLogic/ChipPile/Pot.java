package org.quazarspirit.holdem4j.GameLogic.ChipPile;

import org.quazarspirit.holdem4j.GameLogic.Game;

public class Pot extends ChipCount {
    public Pot(Chip unit) {
        super(unit, 0, LOCATION.TABLE);
    }

    // TODO: Handle different betStructure
    public void add(int betSize) {
        set(get() + betSize);
    }

    public void reset() {
        set(0);
    }
}
