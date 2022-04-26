package com.jne.holdem4j.room_logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PositionHandler {
    static enum POSITION_NAME {
        NONE, SB, BB, UTG, UTG_1, UTG_2, MP_1, MP_2, HIJACK, CO, BTN;

        final static POSITION_NAME[] FREE_POSITION_ORDER = {
                MP_2, MP_1, UTG_2, UTG_1, HIJACK, CO, UTG, BTN
        };

    }
    final private ArrayList<POSITION_NAME> _free_positions = new ArrayList<POSITION_NAME>();

    // Position() {}

    void update(int maxTableSize, int playerCount) {
        _free_positions.clear();

        _free_positions.addAll(Arrays.asList(POSITION_NAME.FREE_POSITION_ORDER)
            .subList(0, (maxTableSize - playerCount)));
    }

    public int getFreePositionCount() {
        return _free_positions.size();
    }

    public POSITION_NAME pickFreePosition() {
        int fp_size = _free_positions.size();

        if (fp_size == 0) {return POSITION_NAME.NONE;}

        POSITION_NAME freePos = _free_positions.get(fp_size -1);
        _free_positions.remove(freePos);
        return freePos;
    }

    public boolean releasePosition(POSITION_NAME position_name) {
        if (_free_positions.contains(position_name)) {
            // Means that position name is already free
            return false;
        }

        int index = Arrays.asList(POSITION_NAME.FREE_POSITION_ORDER).indexOf(position_name);
        index = Math.min(_free_positions.size(), index);
        _free_positions.add(index, position_name);
        return true;
    }
}
