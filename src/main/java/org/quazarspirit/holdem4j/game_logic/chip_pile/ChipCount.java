package org.quazarspirit.holdem4j.game_logic.chip_pile;

public class ChipCount implements IChipCount {
    enum LOCATION {
        PLAYER_STACK, PLAYER_BET_AREA, TABLE
    }
    private final Chip _unit;
    protected int _sb_count;

    protected LOCATION _location = LOCATION.TABLE;

    ChipCount(Chip unit) {
        _unit = unit;
    }

    ChipCount(Chip unit, int count) {
        this(unit);
        set(count);
    }

    ChipCount(Chip unit, int count, LOCATION location) {
        this(unit, count);
        location = _location;
    }

    /**
     * @return
     */
    @Override
    public boolean isValid() {
        return false;
    }

    /**
     * set number of SB in Chip unit
     */
    public void set(int count)  {
        _sb_count = count;
    }

    /**
     * @return Number of SB in Chip unit
     */
    public int get() {
        return _sb_count;
    }

    public String asString() {
        return _sb_count + " SB (SB = " + _unit.asString() + ")";
    }
}
