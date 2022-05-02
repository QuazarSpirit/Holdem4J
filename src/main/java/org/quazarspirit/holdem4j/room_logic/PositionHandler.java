package org.quazarspirit.holdem4j.room_logic;

import java.util.ArrayList;
import java.util.Arrays;
public class PositionHandler {
    public static enum POSITION_NAME {
        NONE, SB, BB, UTG, UTG_1, UTG_2, MP_1, MP_2, HIJACK, CO, BTN;

        final static POSITION_NAME[] FREE_POSITION_ORDER = {
                MP_2, MP_1, UTG_2, UTG_1, HIJACK, CO, UTG, BTN
        };

        final static POSITION_NAME[] SEAT_POSITION_ORDER = {
                SB, BB, BTN, UTG, CO, HIJACK, UTG_1, UTG_2, MP_1, MP_2
        };
    }
    final private ArrayList<POSITION_NAME> _free_positions = new ArrayList<POSITION_NAME>();
    final private ArrayList<POSITION_NAME> _used_positions = new ArrayList<POSITION_NAME>();

    // Position() {}

    void update(int maxTableSize, int playerCount) {
        reset();

        // playerCount should always be superior to 2 to start
        // playing so both positions are prefilled with SB and BB
        _used_positions.addAll(Arrays.asList(POSITION_NAME.SEAT_POSITION_ORDER).subList(0, 2));


        _free_positions.addAll(Arrays.asList(POSITION_NAME.FREE_POSITION_ORDER)
            .subList(0, (maxTableSize - playerCount)));

        _used_positions.addAll(Arrays.asList(POSITION_NAME.SEAT_POSITION_ORDER)
            .subList(2, playerCount));
    }

    void reset() {
        _free_positions.clear();
        _used_positions.clear();
    }

    public int getFreePositionCount() {
        return _free_positions.size();
    }

    public POSITION_NAME pickFreePosition() {
        int fp_size = _free_positions.size();

        if (fp_size == 0) {return POSITION_NAME.NONE;}

        POSITION_NAME freePos = _free_positions.get(fp_size -1);
        if (_used_positions.contains(freePos) || ! _free_positions.contains(freePos) ) {
            // Means that position name is already free
            return POSITION_NAME.NONE;
        }
        _free_positions.remove(freePos);

        int index = Arrays.asList(POSITION_NAME.SEAT_POSITION_ORDER).indexOf(freePos);
        index = Math.min(_used_positions.size(), index);
        _used_positions.add(index, freePos);
        return freePos;
    }

    public boolean releasePosition(POSITION_NAME position_name) {
        if  (_free_positions.contains(position_name)
            && ! _used_positions.contains(position_name)) {
            // Means that position name is already free
            return false;
        }

        int index = Arrays.asList(POSITION_NAME.FREE_POSITION_ORDER).indexOf(position_name);
        // Minified index to insert at the end of _free_position if free_position_count < index;
        index = Math.min(_free_positions.size(), index);

        try {
            _free_positions.add(index, position_name);
        } catch (Exception e) {
            return false;
        }

        _used_positions.remove(position_name);
        return true;
    }
}
