package org.quazarspirit.holdem4j.PlayerLogic.PlayerSeat;

import org.quazarspirit.holdem4j.PlayerLogic.Player.IPlayer;
import org.quazarspirit.holdem4j.RoomLogic.PositionEnum;
import org.quazarspirit.holdem4j.RoomLogic.PositionHandler;

/*
 * Class that simplifies player position handling
 * Because position name is dynamic but not seat
 */
public class PlayerSeat implements IPlayerSeat {
    private final IPlayer _player;
    private final int _seatNumber;
    private PositionEnum _positionName = PositionEnum.NONE;

    public PlayerSeat(IPlayer player, int seatNumber) {
        _player = player;
        _seatNumber = seatNumber;
    }

    PlayerSeat(IPlayer player, int seatNumber, PositionEnum positionName) {
        this(player, seatNumber);
        _positionName = positionName;
    }

    public void setPosition(PositionEnum positionName) {
        _positionName = positionName;
    }

    /**
     * @return
     */
    @Override
    public String asString() {
        return _player.getUsername() + " Seat_" + _seatNumber + " " + _positionName;
    }

    public PositionEnum getPosition() {
        return _positionName;
    }

    public IPlayer getPlayer() {
        return _player;
    }

    /**
     * @return seatNumber of defined player
     */
    @Override
    public int getSeatNumber() {
        return _seatNumber;
    }

}
