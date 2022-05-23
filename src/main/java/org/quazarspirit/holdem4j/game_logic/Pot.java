package org.quazarspirit.holdem4j.game_logic;

public class Pot {
    private int _size = 0;

    private Game.BET_STRUCTURE _betStructure = Game.BET_STRUCTURE.NO_LIMIT;

    public Pot(Game.BET_STRUCTURE betStructure) {
        _betStructure = betStructure;
    }

    int getSize() {return _size; }

    // TODO: Handle different betStructure
    void addToPot(int betSize) { _size += betSize; }

    void reset() {_size = 0;}
}
