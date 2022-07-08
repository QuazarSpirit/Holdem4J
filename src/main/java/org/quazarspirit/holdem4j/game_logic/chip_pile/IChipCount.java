package org.quazarspirit.holdem4j.game_logic.chip_pile;

public interface IChipCount {
    boolean isValid();
    void set(int count);

    int get();
    String asString();
}