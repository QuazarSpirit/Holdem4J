package org.quazarspirit.holdem4j.game_logic;

public class Game {
    public enum VARIANT {
        HOLDEM, OMAHA, DRAW
    }

    public enum BET_STRUCTURE {
        FIXED_LIMIT, POT_LIMIT, NO_LIMIT
    }

    private BET_STRUCTURE _bet_structure = BET_STRUCTURE.NO_LIMIT;
    private VARIANT _variant = VARIANT.HOLDEM;

    Game (VARIANT variant, BET_STRUCTURE bet_structure) {
        this._bet_structure = bet_structure;
        this._variant = variant;
    }
}
