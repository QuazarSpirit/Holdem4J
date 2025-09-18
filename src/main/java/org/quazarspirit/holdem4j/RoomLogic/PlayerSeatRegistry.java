package org.quazarspirit.holdem4j.RoomLogic;

import org.json.JSONObject;
import org.quazarspirit.Utils.ImmutableKV;
import org.quazarspirit.Utils.PubSub.Event;
import org.quazarspirit.Utils.PubSub.IEventType;
import org.quazarspirit.Utils.PubSub.ISubscriber;
import org.quazarspirit.Utils.PubSub.Publisher;
import org.quazarspirit.holdem4j.GameLogic.Game;
import org.quazarspirit.holdem4j.PlayerLogic.PlayerIntentEnum;
import org.quazarspirit.holdem4j.PlayerLogic.Player.IPlayer;
import org.quazarspirit.holdem4j.PlayerLogic.Player.NullPlayer;
import org.quazarspirit.holdem4j.PlayerLogic.PlayerSeat.IPlayerSeat;
import org.quazarspirit.holdem4j.PlayerLogic.PlayerSeat.NullPlayerSeat;
import org.quazarspirit.holdem4j.PlayerLogic.PlayerSeat.PlayerSeat;
import org.quazarspirit.holdem4j.RoomLogic.Table.Table;

import java.util.*;

public class PlayerSeatRegistry extends Publisher implements ISubscriber {
    public enum EventEnum implements IEventType {
        ADD, REMOVE, BIND;
    }

    public enum FilterEnum {
        ALL, BOUND_SEAT
    }

    protected HashMap<Integer, ImmutableKV<IPlayer, IPlayerSeat>> playersSeat = new HashMap<>();

    final private ArrayList<ImmutableKV<IPlayer, PlayerIntentEnum>> _waitingPlayers = new ArrayList<>();

    final static private ImmutableKV<IPlayer, IPlayerSeat> EMPTY_SEAT = new ImmutableKV<>(
            (IPlayer) NullPlayer.GetSingleton(), (IPlayerSeat) NullPlayerSeat.GetSingleton());

    final private int _maxSeatCount;

    final protected PositionHandler _positionHandler;

    public PlayerSeatRegistry(Game game) {
        _maxSeatCount = game.getMaxSeatsCount();
        reset();

        _positionHandler = new PositionHandler();
        this.addSubscriber(_positionHandler);
    }

    private void reset() {
        for (int i = 0; i < _maxSeatCount; i += 1) {
            playersSeat.put(i, EMPTY_SEAT);
        }
    }

    public HashMap<Integer, ImmutableKV<IPlayer, IPlayerSeat>> getPlayersSeat() {
        return new HashMap<>(playersSeat);
    }

    public boolean add(IPlayer player) {
        if (hasPlayer(player)) {
            System.out.println("Player is already registered");
            return false;
        }

        int playerSeatIndex = getFirstEmptySeat();
        System.out.println("Player seat: " + playerSeatIndex);

        if (playerSeatIndex == -1) {
            System.out.println("No seat available");
            return false;
        }

        playersSeat.put(playerSeatIndex, new ImmutableKV<>(player, new PlayerSeat(player, playerSeatIndex)));
        return true;
    }

    /**
     * TODO: Migrate to events
     */
    public boolean remove(IPlayer player) {
        if (!hasPlayer(player)) {
            return false;
        }

        playersSeat.put(getPlayerSeatNumber(player), EMPTY_SEAT);

        return true;
    }

    private void sendAllocateEvent() {
        JSONObject eventData = new JSONObject();
        eventData.put("type", PositionHandler.EventEnum.ALLOCATE);
        eventData.put("max_seat_count", _maxSeatCount);
        eventData.put("player_count", size());
        publish(eventData);
    }

    public void bindPositions(int tableRoundCount) {
        // Utils.Log("bindPositions: " + size());
        if (size() == 0) {
            return;
        }

        sendAllocateEvent();
        ArrayList<PositionEnum> usedPos = _positionHandler.getUsed();
        ArrayList<ImmutableKV<IPlayer, IPlayerSeat>> realPlayers = getRealPlayers();

        int shift = tableRoundCount % size();
        // Utils.Log("bindPositions shift : " + shift + " " + tableRoundCount);

        for (int i = shift; i < size() + shift; i += 1) {
            int index = 0;
            if (i < size()) {
                index = i;
            } else {
                index = i - size();
            }
            ImmutableKV<IPlayer, IPlayerSeat> kv = realPlayers.get(i - shift);
            // Utils.Log(usedPos.get(index));
            kv.getValue().setPosition(usedPos.get(index));
        }
    }

    // Here first player with defined position do position.next()
    // and players after that use next positions
    public void updatePlayerPositions(ArrayList<PositionEnum> usedPos) {
        ArrayList<Integer> seatIndexes = new ArrayList<>();
        int index = _maxSeatCount;
        boolean found = false;
        for (Integer key : playersSeat.keySet()) {
            ImmutableKV<IPlayer, IPlayerSeat> kv = playersSeat.get(key);
            if (kv != EMPTY_SEAT) {
                seatIndexes.add(kv.getValue().getSeatNumber());
                if (kv.getValue().getPosition() != PositionEnum.NONE) {
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

        if (usedPos.size() != sortedSeatIndexes.length) {
            return;
        }

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
        for (Integer key : playersSeat.keySet()) {
            ImmutableKV<IPlayer, IPlayerSeat> kv = playersSeat.get(key);
            if (kv == EMPTY_SEAT) {
                index = Math.min(index, key);
                found = true;
            }
        }

        if (!found) {
            return -1;
        }
        return index;
    }

    public IPlayer getPlayerFromSeatNumber(int seatNumber) {
        for (Map.Entry<Integer, ImmutableKV<IPlayer, IPlayerSeat>> kv : playersSeat.entrySet()) {
            if (kv.getKey() == seatNumber) {
                return kv.getValue().getKey();
            }
        }

        return NullPlayer.GetSingleton();
    }

    public IPlayerSeat getSeatFromPlayer(IPlayer playerToCheck) {
        for (ImmutableKV<IPlayer, IPlayerSeat> kv : playersSeat.values()) {
            if (kv.getKey() == playerToCheck) {
                return kv.getValue();
            }
        }

        return NullPlayerSeat.GetSingleton();
    }

    public int getPlayerSeatNumber(IPlayer playerToCheck) {
        return getSeatFromPlayer(playerToCheck).getSeatNumber();
    }

    public boolean hasPlayer(IPlayer playerToCheck) {
        return getPlayerSeatNumber(playerToCheck) != -1;
    }

    public int getCurrentPlayerCount() {
        return getRealPlayers().size();
    }

    public String asString() {
        return asString(FilterEnum.ALL);
    }

    public String asString(FilterEnum filter) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ImmutableKV<IPlayer, IPlayerSeat> kv : playersSeat.values()) {
            if (filter == FilterEnum.ALL ||
                    (filter == FilterEnum.BOUND_SEAT && kv != EMPTY_SEAT)) {
                stringBuilder.append(kv.getValue().asString()).append("\n");
            }
        }

        return stringBuilder.toString();
    }

    public ArrayList<ImmutableKV<IPlayer, IPlayerSeat>> getRealPlayers() {
        ArrayList<ImmutableKV<IPlayer, IPlayerSeat>> realPlayers = new ArrayList<>();
        for (ImmutableKV<IPlayer, IPlayerSeat> kv : playersSeat.values()) {
            if (kv != EMPTY_SEAT) {
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

    public ArrayList<PositionEnum> getUsedPositions() {
        return _positionHandler.getUsed();
    }

    public ArrayList<PositionEnum> getPlayingPositions() {
        return _positionHandler.getPlaying();
    }

    public int size() {
        return getRealPlayers().size();
    }

    @Override
    public void update(Event event) {
        if (event.getType() == EventEnum.BIND) {
            Table table = (Table) event.source;
            int table_round_count = event.data.getInt("table_round_count");
            bindPositions(table_round_count);
        } else if (event.getType() == EventEnum.ADD) {

        } else if (event.getType() == EventEnum.REMOVE) {

        }
    }
}
