package org.quazarspirit.holdem4j.PlayerLogic.PlayerSeat;

import org.quazarspirit.holdem4j.PlayerLogic.Player.IPlayer;
import org.quazarspirit.holdem4j.PlayerLogic.Player.NullPlayer;
import org.quazarspirit.holdem4j.RoomLogic.PositionEnum;

public class NullPlayerSeat implements IPlayerSeat {
    private final static IPlayerSeat _singleton = new NullPlayerSeat();

    public static IPlayerSeat GetSingleton() {
        return _singleton;
    }

    private final IPlayer _player = NullPlayer.GetSingleton();
    private final int _seatNumber = -1;
    private final PositionEnum _positionName = PositionEnum.NONE;

    private NullPlayerSeat() {
    }

    public PositionEnum getPosition() {
        return _positionName;
    }

    public IPlayer getPlayer() {
        return _player;
    }

    @Override
    public int getSeatNumber() {
        return _seatNumber;
    }

    public void setPosition(PositionEnum positionName) {
    }

    @Override
    public String asString() {
        return _player.getUsername() + " Seat_" + _seatNumber + " " + _positionName;
    }
}
