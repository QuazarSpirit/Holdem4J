package org.quazarspirit.holdem4j.GameLogic;

import org.quazarspirit.Utils.Utils;
import org.quazarspirit.Utils.PubSub.Event;
import org.quazarspirit.Utils.PubSub.IEventType;
import org.quazarspirit.Utils.PubSub.ISubscriber;
import org.quazarspirit.holdem4j.RoomLogic.PositionEnum;

/**
 *
 */
public class BettingRound implements ISubscriber {
    public enum Event implements IEventType {
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
        if (event.getType() == BettingRound.Event.NEXT) {
            nextPhase();
        } else if (event.getType() == BettingRound.Event.RESET) {
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

    @Override
    public void update(org.quazarspirit.Utils.PubSub.Event event) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
