package org.quazarspirit.holdem4j.game_logic;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

/**
 *
 */
public class Round {
    public enum ROUND_PHASE {
        // Specific to Game.VARIANTS.HOLDEM
        // TODO: Move to rule
        PRE_FLOP, FLOP, TURN, RIVER;
        public ROUND_PHASE getNext() {
            return this.ordinal() < ROUND_PHASE.values().length - 1
                    ? ROUND_PHASE.values()[this.ordinal() + 1]
                    : PRE_FLOP;
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
    public static HashMap<ROUND_PHASE, Integer> ROUND_CARD_COUNT = (HashMap<ROUND_PHASE, Integer>) Map.ofEntries(
            entry(ROUND_PHASE.PRE_FLOP, 0),
            entry(ROUND_PHASE.FLOP, 3),
            entry(ROUND_PHASE.TURN, 4),
            entry(ROUND_PHASE.RIVER, 5)
    );

    private ROUND_PHASE _roundPhase = ROUND_PHASE.PRE_FLOP;

    public void next() {
        _roundPhase = _roundPhase.getNext();
    }

    public ROUND_PHASE getRoundPhase() {
        return _roundPhase;
    }
}
