package org.quazarspirit.holdem4j.room_logic;

import org.quazarspirit.holdem4j.game_logic.Round;
import org.quazarspirit.utils.publisher_subscriber_pattern.Event;
import org.quazarspirit.utils.publisher_subscriber_pattern.ISubscriber;

import java.util.*;

public class Position implements ISubscriber {
    public enum NAME {
        NONE(-1), SB(0), BB(1), UTG(2), UTG_1(3), UTG_2(4),
        MP_1(5), MP_2(6), HIJACK(7), CO(8), BTN(9);

        private final int _index;

        NAME(int index) { _index = index; }
        public int getIndex() { return _index; }

        final static NAME[] DEFAULT = {
            SB, BB, UTG, UTG_1, UTG_2, MP_1, MP_2, HIJACK, CO, BTN
        };

        final static NAME[] PRIORITY_ORDER = {
            SB, BB, BTN, UTG, CO, HIJACK, UTG_1, UTG_2, MP_1, MP_2
        };
    }
    final private ArrayList<NAME> _used_positions = new ArrayList<>();
    final private ArrayList<NAME> _free_positions = new ArrayList<>();
    final private ArrayList<NAME> _playing_positions = new ArrayList<>();
    final private ArrayList<NAME> _waiting_release_used = new ArrayList<>();
    final private ArrayList<NAME> _waiting_release_playing = new ArrayList<>();
    /*
    final private ITable _table;
    Position(ITable table) {
        _table = table;
    }
     */

    /**
     * Method called when publisher send an event
     */
    @Override
    public void update(Event event) {
        if(event.data.get("type")  == Round.EVENT.NEXT) {
            purgeWaitingPositions();
        }
    }

    private void reset() {
        _free_positions.clear();
        _used_positions.clear();
        _playing_positions.clear();
        _waiting_release_used.clear();
        _waiting_release_playing.clear();
    }
    public void update(int maxPlayerCount, int currentPlayerCount) {
        reset();

        _used_positions.addAll(Arrays.asList(NAME.DEFAULT));
        _used_positions.removeAll(Arrays.asList(NAME.PRIORITY_ORDER)
            .subList(currentPlayerCount, NAME.PRIORITY_ORDER.length));

        _free_positions.addAll(Arrays.asList(NAME.PRIORITY_ORDER)
            .subList(currentPlayerCount, maxPlayerCount));

        _playing_positions.addAll(_used_positions);
    }
    public int getFreeCount() { return _free_positions.size(); }
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
    public void removeUsed(NAME positionName) {
        // This doesn't mean that player CAN'T disconnect from table
        // this only means that the POSITION won't be deleted before next round phase
        _waiting_release_used.add(positionName);
    }
    public void releaseUsed(NAME positionName) {
        if  (_free_positions.contains(positionName)
            && ! _used_positions.contains(positionName)) {
            // Means that position name is already free
            return;
        }

        int index = Arrays.asList(NAME.DEFAULT).indexOf(positionName);
        // Minified index to insert at the end of _free_position if free_position_count < index;
        index = Math.min(_free_positions.size(), index);

        try {
            _free_positions.add(index, positionName);
        } catch (Exception e) {
            return;
        }

        _used_positions.remove(positionName);
        _playing_positions.remove(positionName);
    }

    public void removePlaying(NAME positionName) {
        // This doesn't mean that player CAN still play on table
        // this only means that the POSITION won't be deleted before next round phase
        _waiting_release_playing.add(positionName);
    }
    public synchronized void releasePlaying(NAME positionName) {
        // Needs to remove after round phase because of concurrent modification
        _playing_positions.remove(positionName);
        _waiting_release_playing.remove(positionName);
    }

    public void purgeWaitingPositions() {

        // Maybe tell roundPhase instead of this
        //Round.ROUND_PHASE roundPhase = _table.getRound().getRoundPhase();
        Round.ROUND_PHASE roundPhase = Round.ROUND_PHASE.STASIS;

        if(roundPhase == Round.ROUND_PHASE.STASIS) {
            // Release all players waiting to exit table
            for(Position.NAME positionName: _waiting_release_used) {
                releaseUsed(positionName);
            }
        } else {
            // Release players waiting to fold (doesn't mean that they can play)
            for(Position.NAME positionName: _waiting_release_playing) {
                releasePlaying(positionName);
            }
        }
    }

    public ArrayList<NAME> getUsed() {
        _used_positions.sort(new Comparator<NAME>() {
            @Override
            public int compare(NAME o1, NAME o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });

        // Return new array from _playing_positions to restrain modification of original array
        return new ArrayList<>(_used_positions);
    }
    public ArrayList<NAME> getPlaying() {
        _playing_positions.sort(new Comparator<NAME>() {
            @Override
            public int compare(NAME o1, NAME o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });

        // Return new array from _playing_positions to restrain modification of original array
        return new ArrayList<>(_playing_positions);
    }
}
