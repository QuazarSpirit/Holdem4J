package org.quazarspirit.holdem4j.player_logic.player_seat;

import org.quazarspirit.holdem4j.player_logic.player.IPlayer;
import org.quazarspirit.holdem4j.player_logic.player.NullPlayer;
import org.quazarspirit.holdem4j.room_logic.POSITION;

public class NullPlayerSeat implements IPlayerSeat {
    private final static IPlayerSeat _singleton = new NullPlayerSeat();

    public static IPlayerSeat GetSingleton() {
        return _singleton;
    }
    private final IPlayer _player = NullPlayer.GetSingleton();
    private final int _seatNumber = -1;
    private final POSITION _positionName = POSITION.NONE;
    private NullPlayerSeat() {}

    public POSITION getPosition()  {
        return _positionName;
    }

    public IPlayer getPlayer() {
        return _player;
    }

    @Override
    public int getSeatNumber() {
        return _seatNumber;
    }

    public void setPosition(POSITION positionName) {}

    @Override
    public String asString() {
        return _player.getUsername() + " Seat_" + _seatNumber + " " + _positionName;
    }
}
