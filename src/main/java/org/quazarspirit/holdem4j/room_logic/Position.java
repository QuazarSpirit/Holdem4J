package org.quazarspirit.holdem4j.room_logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Position {
    public enum NAME {
        NONE(-1), SB(0), BB(1), UTG(2), UTG_1(3), UTG_2(4),
        MP_1(5), MP_2(6), HIJACK(7), CO(8), BTN(9);

        private int _index;

        NAME(int index) { _index = index; }
        public int getIndex() { return _index; }

        final static NAME[] DEFAULT = {
            SB, BB, UTG, UTG_1, UTG_2, MP_1, MP_2, HIJACK, CO, BTN
        };

        final static NAME[] PRIORITY_ORDER = {
            SB, BB, BTN, UTG, CO, HIJACK, UTG_1, UTG_2, MP_1, MP_2
        };
    }
    private ArrayList<NAME> _used_positions = new ArrayList<>();
    final private ArrayList<NAME> _free_positions = new ArrayList<>();
    final private ArrayList<NAME> _playing_positions = new ArrayList<>();
    private void reset() {
        _free_positions.clear();
        _used_positions.clear();
        _playing_positions.clear();
    }
    public void update(int maxPlayerCount, int currentPlayerCount) {
        reset();

        _used_positions = new ArrayList<>(Arrays.asList(NAME.DEFAULT));
        _used_positions.removeAll(Arrays.asList(NAME.PRIORITY_ORDER)
            .subList(currentPlayerCount, NAME.PRIORITY_ORDER.length));

        _free_positions.addAll(Arrays.asList(NAME.PRIORITY_ORDER)
            .subList(currentPlayerCount, maxPlayerCount));

        _playing_positions.addAll(_used_positions);
    }
    public int getFreeCount() {
        return _free_positions.size();
    }
    public NAME pickFree() {
        int fp_size = _free_positions.size();
        if (fp_size == 0) {return NAME.NONE;}

        NAME freePos = _free_positions.get(0);

        if (_used_positions.contains(freePos)) {
            // Means that position name is already picked
            System.out.println("position name is already picked");
            return NAME.NONE;
        }
        _free_positions.remove(freePos);

        int index = Arrays.asList(NAME.PRIORITY_ORDER).indexOf(freePos);
        index = Math.min(_used_positions.size(), index);
        _used_positions.add(index, freePos);
        _playing_positions.add(index, freePos);
        return freePos;
    }
    public boolean release(NAME _name) {
        if  (_free_positions.contains(_name)
            && ! _used_positions.contains(_name)) {
            // Means that position name is already free
            return false;
        }

        int index = Arrays.asList(NAME.DEFAULT).indexOf(_name);
        // Minified index to insert at the end of _free_position if free_position_count < index;
        index = Math.min(_free_positions.size(), index);

        try {
            _free_positions.add(index, _name);
        } catch (Exception e) {
            return false;
        }

        _used_positions.remove(_name);
        _playing_positions.remove(_name);
        return true;
    }
    public void releasePlaying(NAME _name) { _playing_positions.remove(_name); }
    public ArrayList<NAME> getUsed() {
        _used_positions.sort(new Comparator<NAME>() {
            @Override
            public int compare(NAME o1, NAME o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });

        return _used_positions;
    }
    public ArrayList<NAME> getPlaying() {
        _playing_positions.sort(new Comparator<NAME>() {
            @Override
            public int compare(NAME o1, NAME o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });

        // System.out.println("AAA ! " + _playing_positions);
        return _playing_positions;
    }
}
