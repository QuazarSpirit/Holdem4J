package org.quazarspirit.holdem4j.game_logic;

import org.quazarspirit.utils.publisher_subscriber_pattern.IEventType;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Round {
    public enum ROUND_PHASE {
        // Specific to Game.VARIANTS.TEXAS_HOLDEM / OMAHA_HOLDEM
        // TODO: Move to

        // Phase that permits player to join/leave or rebuy
        STASIS(0),
        PRE_FLOP(0), FLOP(3), TURN(1), RIVER(1);

        private int _drawCount;
        ROUND_PHASE(int drawCount) {
            _drawCount = drawCount;
        }
        public ROUND_PHASE getNext() {
            return this.ordinal() < ROUND_PHASE.values().length - 1
                    ? ROUND_PHASE.values()[this.ordinal() + 1]
                    : STASIS;
        }

        public int getDrawCount() {
            return _drawCount;
        }

    }

    public enum EVENT implements IEventType {
        NEXT;
    }

    // TODO: Refactor as rule
    public static Map<ROUND_PHASE, Integer> ROUND_CARD_COUNT = new HashMap<>(){
        {
            put(ROUND_PHASE.PRE_FLOP, 0);
            put(ROUND_PHASE.FLOP, 3);
            put(ROUND_PHASE.TURN, 4);
            put(ROUND_PHASE.RIVER, 5);
        }
    };


    private ROUND_PHASE _roundPhase = ROUND_PHASE.STASIS;

    public void next() {
        _roundPhase = _roundPhase.getNext();
    }

    public ROUND_PHASE getRoundPhase() {
        return _roundPhase;
    }
}
