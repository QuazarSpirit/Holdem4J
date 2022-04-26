package com.jne.holdem4j.game_logic;

public class Game {
    enum VARIANT {
        NONE, HOLDEM, OMAHA, DRAW
    }

    enum BET_STRUCTURE {
        NONE, FIXED_LIMIT, POT_LIMIT, NO_LIMIT
    }

    private BET_STRUCTURE _bet_structure = BET_STRUCTURE.NONE;
    private VARIANT _variant = VARIANT.NONE;

    Game (VARIANT variant, BET_STRUCTURE bet_structure) {
        this._bet_structure = bet_structure;
        this._variant = variant;
    }
}
