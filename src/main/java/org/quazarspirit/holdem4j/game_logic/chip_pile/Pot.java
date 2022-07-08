package org.quazarspirit.holdem4j.game_logic.chip_pile;

import org.quazarspirit.holdem4j.game_logic.Game;

public class Pot extends ChipCount {
    public Pot(Chip unit) {
        super(unit, 0, LOCATION.TABLE);
    }

    // TODO: Handle different betStructure
    public void add(int betSize) { set(get() + betSize); }

    public void reset() { set(0); }
}
