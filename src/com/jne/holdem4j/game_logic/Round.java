package com.jne.holdem4j.game_logic;

public class Round {
    enum ROUND_STATE {
        NONE, PRE_FLOP, FLOP, TURN, RIVER;
        public ROUND_STATE getNext() {
            return this.ordinal() < ROUND_STATE.values().length - 1
                    ? ROUND_STATE.values()[this.ordinal() + 1]
                    : NONE;
        }

        public int getDrawCount() {
            int drawCount = 0;
            switch(this) {
                case FLOP -> drawCount = 3;
                case TURN, RIVER -> drawCount = 1;
            }

            return drawCount;
        }
    }

    private ROUND_STATE _roundState = ROUND_STATE.NONE;

    public void next() {
        _roundState = _roundState.getNext();
    }
}
