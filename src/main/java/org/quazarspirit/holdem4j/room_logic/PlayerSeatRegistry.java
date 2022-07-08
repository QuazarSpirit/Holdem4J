package org.quazarspirit.holdem4j.room_logic;

import org.json.JSONObject;
import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.holdem4j.player_logic.player.IPlayer;
import org.quazarspirit.holdem4j.player_logic.player.NullPlayer;
import org.quazarspirit.holdem4j.player_logic.player_seat.IPlayerSeat;
import org.quazarspirit.holdem4j.player_logic.player_seat.NullPlayerSeat;
import org.quazarspirit.holdem4j.player_logic.player_seat.PlayerSeat;
import org.quazarspirit.utils.ImmutableKV;
import org.quazarspirit.utils.publisher_subscriber_pattern.Event;
import org.quazarspirit.utils.publisher_subscriber_pattern.IEventType;
import org.quazarspirit.utils.publisher_subscriber_pattern.ISubscriber;
import org.quazarspirit.utils.publisher_subscriber_pattern.Publisher;

import java.util.*;

// TODO: UNIT TEST PLS PLS PLS ITS IMPORTANT


public class PlayerSeatRegistry extends Publisher implements ISubscriber {
    public enum EVENT implements IEventType {
        ADD, REMOVE, BIND;
    }

    public enum FILTER {
        ALL, BOUND_SEAT
    }

    protected HashMap<Integer, ImmutableKV<IPlayer, IPlayerSeat>> playersSeat = new HashMap<>();
    final static private ImmutableKV<IPlayer, IPlayerSeat> EMPTY_SEAT = new ImmutableKV<>((IPlayer) NullPlayer.GetSingleton(), (IPlayerSeat) NullPlayerSeat.GetSingleton());

    final private int _maxSeatCount;

    final protected PositionHandler _positionHandler;

    public PlayerSeatRegistry(Game game) {
        _maxSeatCount = game.getMaxSeatsCount();
        reset();

        _positionHandler = new PositionHandler();
        this.addSubscriber(_positionHandler);
    }

    private void reset() {
        for(int i = 0; i < _maxSeatCount; i+=1) {
            playersSeat.put(i, EMPTY_SEAT);
        }
    }

    public HashMap<Integer, ImmutableKV<IPlayer, IPlayerSeat>> getPlayersSeat() {
        return new HashMap<>(playersSeat);
    }

    /**
     * TODO: Migrate to events
     */
    public boolean add(IPlayer player) {
        if (_containsPlayer(player)) { return false; }

        int playerSeatIndex = getFirstEmptySeat();

        if (playerSeatIndex == -1) {
            return false;
        }

        playersSeat.put(playerSeatIndex, new ImmutableKV<>(player, new PlayerSeat(player, playerSeatIndex)));
        return true;
    }

    /**
     * TODO: Migrate to events
     */
    public boolean remove(IPlayer player) {
        if (!_containsPlayer(player)) { return false; }

        playersSeat.put(getPlayerSeatNumber(player), EMPTY_SEAT);

        return true;
    }

    private void sendAllocateEvent() {
        JSONObject eventData = new JSONObject();
        eventData.put("type", PositionHandler.EVENT.ALLOCATE);
        eventData.put("max_seat_count", _maxSeatCount);
        eventData.put("player_count", size());
        publish(eventData);
    }

    public void bindPositions(int tableRoundCount) {
        //System.out.println("bindPositions: "  + size());
        if (size() == 0) {
            return;
        }

        sendAllocateEvent();
        ArrayList<POSITION> usedPos = _positionHandler.getUsed();
        ArrayList<ImmutableKV<IPlayer, IPlayerSeat>> realPlayers = getRealPlayers();

        int shift = tableRoundCount % size();
        //System.out.println("bindPositions shift : " + shift + " " + tableRoundCount);

        for (int i = shift; i < size() + shift; i += 1) {
            int index = 0;
            if (i < size()) {
                index = i;
            } else {
                index = i - size();
            }
            ImmutableKV<IPlayer, IPlayerSeat> kv = realPlayers.get(i - shift);
            System.out.println(usedPos.get(index));
            kv.getValue().setPosition(usedPos.get(index));
        }
    }

    // Here first player with defined position do position.next()
    // and players after that use next positions
    public void updatePlayerPositions(ArrayList<POSITION> usedPos) {
        ArrayList<Integer> seatIndexes = new ArrayList<>();
        int index = _maxSeatCount;
        boolean found = false;
        for (Integer key: playersSeat.keySet()) {
            ImmutableKV<IPlayer, IPlayerSeat> kv = playersSeat.get(key);
            if(kv != EMPTY_SEAT) {
                seatIndexes.add(kv.getValue().getSeatNumber());
                if (kv.getValue().getPosition() != POSITION.NONE) {
                    index = Math.min(index, key);
                    found = true;
                }
            }
        }


        if (!found) {
            // Means that table is empty
            return;
        }

        Object[] sortedSeatIndexes = seatIndexes.stream().sorted().toArray();

        if (usedPos.size() != sortedSeatIndexes.length) { return; }

        for (int i = 0; i < sortedSeatIndexes.length; i += 1) {
            ImmutableKV<IPlayer, IPlayerSeat> kv = (ImmutableKV<IPlayer, IPlayerSeat>) sortedSeatIndexes[i];

        }
    }

    /**
     * Returns index of first empty seat else -1
     */
    private int getFirstEmptySeat() {
        int index = _maxSeatCount;
        boolean found = false;
        for (Integer key: playersSeat.keySet()) {
            ImmutableKV<IPlayer, IPlayerSeat> kv = playersSeat.get(key);
            if(kv == EMPTY_SEAT) {
                index = Math.min(index, key);
                found = true;
            }
        }

        if (!found) { return -1; }
        return index;
    }


    public IPlayer getPlayerFromSeatNumber(int seatNumber) {
        for (Map.Entry<Integer, ImmutableKV<IPlayer, IPlayerSeat>> kv : playersSeat.entrySet()) {
            if(kv.getKey() == seatNumber) {
                return kv.getValue().getKey();
            }
        }

        return NullPlayer.GetSingleton();
    }

    public IPlayerSeat getSeatFromPlayer(IPlayer playerToCheck) {
        for (ImmutableKV<IPlayer, IPlayerSeat> kv : playersSeat.values()) {
            if(kv.getKey() == playerToCheck) {
                return kv.getValue();
            }
        }

        return NullPlayerSeat.GetSingleton();
    }

    public int getPlayerSeatNumber(IPlayer playerToCheck) {
        return getSeatFromPlayer(playerToCheck).getSeatNumber();
    }

    public boolean _containsPlayer(IPlayer playerToCheck) {
        return getPlayerSeatNumber(playerToCheck) != -1;
    }

    public String asString() {
        return asString(FILTER.ALL);
    }

    public String asString(FILTER filter) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ImmutableKV<IPlayer, IPlayerSeat> kv : playersSeat.values()) {
            if  (filter == FILTER.ALL ||
                (filter == FILTER.BOUND_SEAT && kv != EMPTY_SEAT)) {
                stringBuilder.append(kv.getValue().asString()).append("\n");
            }
        }

        return stringBuilder.toString();
    }

    public ArrayList<ImmutableKV<IPlayer, IPlayerSeat>> getRealPlayers() {
        ArrayList<ImmutableKV<IPlayer, IPlayerSeat>> realPlayers = new ArrayList<>();
        for (ImmutableKV<IPlayer, IPlayerSeat> kv: playersSeat.values()) {
            if(kv != EMPTY_SEAT) {
                realPlayers.add(kv);
            }
        }

        realPlayers.sort(new Comparator<ImmutableKV<IPlayer, IPlayerSeat>>() {
            @Override
            public int compare(ImmutableKV<IPlayer, IPlayerSeat> o1, ImmutableKV<IPlayer, IPlayerSeat> o2) {
                return o1.getValue().getSeatNumber() - o2.getValue().getSeatNumber();
            }
        });

        return realPlayers;
    }

    public int size() {
        return getRealPlayers().size();
    }

    @Override
    public void update(Event event) {
        if (event.getType() == EVENT.BIND) {
            Table table = (Table) event.source;
            int table_round_count = event.data.getInt("table_round_count");
            bindPositions(table_round_count);
        } else if (event.getType() == EVENT.ADD) {

        } else if (event.getType() == EVENT.REMOVE) {

        }
    }
}
