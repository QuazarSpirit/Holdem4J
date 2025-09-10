package org.quazarspirit.holdem4j.GameLogic.ChipPile;

public interface IChipCount {
    boolean isValid();

    void set(int count);

    int get();

    String asString();
}