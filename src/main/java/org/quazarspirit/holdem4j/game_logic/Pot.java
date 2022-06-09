package org.quazarspirit.holdem4j.game_logic;

public class Pot {
    private int _size = 0;
    private Game.BET_STRUCTURE _betStructure;

    public Pot(Game.BET_STRUCTURE betStructure) {
        _betStructure = betStructure;
    }

    public int getSize() {return _size; }

    // TODO: Handle different betStructure
    public void add(int betSize) { _size += betSize; }

    public void reset() {_size = 0;}
}
