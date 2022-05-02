package org.quazarspirit.holdem4j.game_logic;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class Round {
    enum ROUND_STATE {
        // Specific to Game.VARIANTS.HOLDEM
        // TODO: Move to rule
        NONE, PRE_FLOP, FLOP, TURN, RIVER;
        public ROUND_STATE getNext() {
            return this.ordinal() < ROUND_STATE.values().length - 1
                    ? ROUND_STATE.values()[this.ordinal() + 1]
                    : NONE;
        }

        public int getDrawCount() {
            int drawCount = 0;
            switch(this) {
                case FLOP        -> drawCount = 3;
                case TURN, RIVER -> drawCount = 1;
            }

            return drawCount;
        }

    }

    // TODO: Refactor as rule
    public static HashMap<ROUND_STATE, Integer> ROUND_CARD_COUNT = (HashMap<ROUND_STATE, Integer>) Map.ofEntries(
            entry(ROUND_STATE.NONE, 0),
            entry(ROUND_STATE.PRE_FLOP, 0),
            entry(ROUND_STATE.FLOP, 3),
            entry(ROUND_STATE.TURN, 4),
            entry(ROUND_STATE.RIVER, 5)
    );

    private ROUND_STATE _roundState = ROUND_STATE.NONE;

    public void next() {
        _roundState = _roundState.getNext();
    }

    public ROUND_STATE getRoundState() {
        return _roundState;
    }
}
