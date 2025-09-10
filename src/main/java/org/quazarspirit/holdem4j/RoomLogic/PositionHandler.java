package org.quazarspirit.holdem4j.RoomLogic;

import org.quazarspirit.Utils.Utils;
import org.quazarspirit.Utils.PubSub.Event;
import org.quazarspirit.Utils.PubSub.IEventType;
import org.quazarspirit.Utils.PubSub.ISubscriber;
import org.quazarspirit.holdem4j.GameLogic.BettingRound;

import java.util.*;

public class PositionHandler implements ISubscriber {
    public enum EVENT implements IEventType {
        ALLOCATE
    }

    final private Utils.CircularArrayList<PositionEnum> _used_positions = new Utils.CircularArrayList<>();
    final private ArrayList<PositionEnum> _free_positions = new ArrayList<>();
    final private Utils.CircularArrayList<PositionEnum> _playing_positions = new Utils.CircularArrayList<>();
    final private ArrayList<PositionEnum> _waiting_release_used = new ArrayList<>();
    final private ArrayList<PositionEnum> _waiting_release_playing = new ArrayList<>();

    /**
     * Method called when publisher send an event
     */
    @Override
    public void update(Event event) {
        // Utils.Log("Got event");

        if (event.data.get("type") == EVENT.ALLOCATE) {
            int max_seat_count = (int) event.data.get("max_seat_count");
            int playerCount = (int) event.data.get("player_count");
            // Utils.Log(max_seat_count + " " + playerCount);
            allocate(max_seat_count, playerCount);
        }
    }

    private void reset() {
        _free_positions.clear();
        _used_positions.clear();
        _playing_positions.clear();
        _waiting_release_used.clear();
        _waiting_release_playing.clear();
    }

    private void allocate(int maxPlayerCount, int currentPlayerCount) {
        reset();

        _used_positions.addAll(Arrays.asList(PositionEnum.DEFAULT));
        _used_positions.removeAll(Arrays.asList(PositionEnum.PRIORITY_ORDER)
                .subList(currentPlayerCount, PositionEnum.PRIORITY_ORDER.length));

        _free_positions.addAll(Arrays.asList(PositionEnum.PRIORITY_ORDER)
                .subList(currentPlayerCount, maxPlayerCount));

        _playing_positions.addAll(_used_positions);
    }

    public int getFreeCount() {
        return _free_positions.size();
    }

    public PositionEnum pickFree() {
        int fp_size = _free_positions.size();
        if (fp_size == 0) {
            return PositionEnum.NONE;
        }

        PositionEnum freePos = _free_positions.get(0);

        if (_used_positions.contains(freePos)) {
            // Means that position name is already picked
            Utils.Log("position name is already picked");
            return PositionEnum.NONE;
        }
        _free_positions.remove(freePos);

        int index = Arrays.asList(PositionEnum.PRIORITY_ORDER).indexOf(freePos);
        index = Math.min(_used_positions.size(), index);
        _used_positions.add(index, freePos);
        _playing_positions.add(index, freePos);
        return freePos;
    }

    public void removeUsed(PositionEnum positionName) {
        // This doesn't mean that player CAN'T disconnect from table
        // this only means that the POSITION won't be deleted before next round phase
        _waiting_release_used.add(positionName);
    }

    public void releaseUsed(PositionEnum positionName) {
        if (_free_positions.contains(positionName)
                && !_used_positions.contains(positionName)) {
            // Means that position name is already free
            return;
        }

        int index = Arrays.asList(PositionEnum.DEFAULT).indexOf(positionName);
        // Minified index to insert at the end of _free_position if free_position_count
        // < index;
        index = Math.min(_free_positions.size(), index);

        try {
            _free_positions.add(index, positionName);
        } catch (Exception e) {
            return;
        }

        _used_positions.remove(positionName);
        _playing_positions.remove(positionName);
    }

    public void removePlaying(PositionEnum positionName) {
        // This doesn't mean that player CAN still play on table
        // this only means that the POSITION won't be deleted before next round phase
        _waiting_release_playing.add(positionName);
    }

    public synchronized void releasePlaying(PositionEnum positionName) {
        // Needs to remove after round phase because of concurrent modification
        _playing_positions.remove(positionName);
        _waiting_release_playing.remove(positionName);
    }

    public void purgeWaitingPositions() {

        // Maybe tell roundPhase instead of this
        // BettingRound.PHASE roundPhase = _table.getRound().getRoundPhase();
        BettingRound.PHASE roundPhase = BettingRound.PHASE.STASIS;

        if (roundPhase == BettingRound.PHASE.STASIS) {
            // Release all players waiting to exit table
            for (PositionEnum positionName : _waiting_release_used) {
                releaseUsed(positionName);
            }
        } else {
            // Release players waiting to fold (doesn't mean that they can play)
            for (PositionEnum positionName : _waiting_release_playing) {
                releasePlaying(positionName);
            }
        }
    }

    public ArrayList<PositionEnum> getUsed() {
        _used_positions.sort(new Comparator<PositionEnum>() {
            @Override
            public int compare(PositionEnum o1, PositionEnum o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });

        // Return new array from _playing_positions to restrain modification of original
        // array
        return new Utils.CircularArrayList<>(_used_positions);
    }

    public ArrayList<PositionEnum> getPlaying() {
        _playing_positions.sort(new Comparator<PositionEnum>() {
            @Override
            public int compare(PositionEnum o1, PositionEnum o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });

        // Return new array from _playing_positions to restrain modification of original
        // array
        return new ArrayList<>(_playing_positions);
    }
}
