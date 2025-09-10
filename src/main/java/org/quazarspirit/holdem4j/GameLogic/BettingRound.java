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
    public enum EventEnum implements IEventType {
        NEXT, RESET
    }

    public enum PhaseEnum {
        // Specific to Game.VARIANTS.TEXAS_HOLDEM / OMAHA_HOLDEM
        // TODO: Move to

        // Phase that permits player to join/leave or rebuy
        STASIS(0),
        PRE_FLOP(0), FLOP(3), TURN(1), RIVER(1),

        SHOWDOWN(0);

        private final int _drawCount;

        PhaseEnum(int drawCount) {
            _drawCount = drawCount;
        }

        public PhaseEnum getNext() {
            return this.ordinal() < PhaseEnum.values().length - 1
                    ? PhaseEnum.values()[this.ordinal() + 1]
                    : STASIS;
        }

        public int getDrawCount() {
            return _drawCount;
        }

        // Used for testing purpose
        public final static PhaseEnum[] DEFAULT = {
                PRE_FLOP, FLOP, TURN, RIVER, SHOWDOWN, STASIS
        };

    }

    private PhaseEnum _roundPhase = PhaseEnum.STASIS;

    @Override
    public void update(EventEnum event) {
        if (event.getType() == BettingRound.EventEnum.NEXT) {
            nextPhase();
        } else if (event.getType() == BettingRound.EventEnum.RESET) {
            reset();
        }
    }

    /**
     * Used for testing purpose and when all players fold before showdown
     */
    private void reset() {
        Utils.Log("Betting round reset");
        _roundPhase = PhaseEnum.STASIS;
    }

    private void nextPhase() {

        _roundPhase = _roundPhase.getNext();
        Utils.Log("Betting round next " + _roundPhase);
    }

    public PhaseEnum getPhase() {
        return _roundPhase;
    }

    @Override
    public void update(org.quazarspirit.Utils.PubSub.Event event) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
