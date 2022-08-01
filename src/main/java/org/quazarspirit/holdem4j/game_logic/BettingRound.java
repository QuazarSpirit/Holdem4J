package org.quazarspirit.holdem4j.game_logic;

import org.quazarspirit.holdem4j.room_logic.POSITION;
import org.quazarspirit.utils.Utils;
import org.quazarspirit.utils.publisher_subscriber_pattern.Event;
import org.quazarspirit.utils.publisher_subscriber_pattern.IEventType;
import org.quazarspirit.utils.publisher_subscriber_pattern.ISubscriber;

/**
 *
 */
public class BettingRound implements ISubscriber {
    public enum EVENT implements IEventType {
        NEXT, RESET
    }

    public enum PHASE {
        // Specific to Game.VARIANTS.TEXAS_HOLDEM / OMAHA_HOLDEM
        // TODO: Move to

        // Phase that permits player to join/leave or rebuy
        STASIS(0),
        PRE_FLOP(0), FLOP(3), TURN(1), RIVER(1),

        SHOWDOWN(0);

        private final int _drawCount;
        PHASE(int drawCount) {
            _drawCount = drawCount;
        }
        public PHASE getNext() {
            return this.ordinal() < PHASE.values().length - 1
                    ? PHASE.values()[this.ordinal() + 1]
                    : STASIS;
        }

        public int getDrawCount() {
            return _drawCount;
        }

        // Used for testing purpose
        public final static PHASE[] DEFAULT = {
                PRE_FLOP, FLOP, TURN, RIVER, SHOWDOWN, STASIS
        };

    }

    private PHASE _roundPhase = PHASE.STASIS;

    @Override
    public void update(Event event) {
        if (event.getType() == BettingRound.EVENT.NEXT) {
            nextPhase();
        } else if (event.getType() == BettingRound.EVENT.RESET) {
            reset();
        }
    }


    /**
     * Used for testing purpose and when all players fold before showdown
     */
    private void reset() {
        Utils.Log("Betting round reset");
        _roundPhase = PHASE.STASIS;
    }

    private void nextPhase() {

        _roundPhase = _roundPhase.getNext();
        Utils.Log("Betting round next " + _roundPhase);
    }

    public PHASE getPhase() {
        return _roundPhase;
    }
}
