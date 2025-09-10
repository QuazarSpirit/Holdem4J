package org.quazarspirit.holdem4j.RoomLogic;

public enum PositionEnum {
        NONE(-1), SB(0), BB(1), UTG(2), UTG_1(3), UTG_2(4),
        MP_1(5), MP_2(6), HIJACK(7), CO(8), BTN(9);

        private final int _index;

        PositionEnum(int index) {
                _index = index;
        }

        public int getIndex() {
                return _index;
        }

        final static PositionEnum[] DEFAULT = {
                        SB, BB, UTG, UTG_1, UTG_2, MP_1, MP_2, HIJACK, CO, BTN
        };

        final static PositionEnum[] PRIORITY_ORDER = {
                        SB, BB, BTN, UTG, CO, HIJACK, UTG_1, UTG_2, MP_1, MP_2
        };
}
