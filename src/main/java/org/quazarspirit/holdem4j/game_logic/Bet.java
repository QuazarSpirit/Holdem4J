package org.quazarspirit.holdem4j.game_logic;

/**
 * Implements betting rules eg: NO_LIMIT, FIXED_LIMIT
 */
public class Bet {
    private int _size;

    public Bet(Game game) {
        _size = game.getBB() * 2;
    }

    Bet(Game game, int size) {
        this(game);

        if(isValid(size)) {
            _size = size;
        }
    }

    boolean isValid(int size) {
        return false;
    }

    public int getSize() {
        return _size;
    }

}
