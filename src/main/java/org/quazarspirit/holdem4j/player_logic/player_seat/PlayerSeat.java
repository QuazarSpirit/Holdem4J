package org.quazarspirit.holdem4j.player_logic.player_seat;

import org.quazarspirit.holdem4j.player_logic.player.IPlayer;
import org.quazarspirit.holdem4j.room_logic.POSITION;
import org.quazarspirit.holdem4j.room_logic.PositionHandler;

/*
 * Class that simplifies player position handling
 * Because position name is dynamic but not seat
 */
public class PlayerSeat implements IPlayerSeat{
    private final IPlayer _player;
    private final int _seatNumber;
    private POSITION _positionName = POSITION.NONE;

    public PlayerSeat(IPlayer player, int seatNumber) {
        _player = player;
        _seatNumber = seatNumber;
    }

    PlayerSeat(IPlayer player, int seatNumber, POSITION positionName) {
        this(player, seatNumber);
        _positionName = positionName;
    }

    public void setPosition(POSITION positionName) {
        _positionName = positionName;
    }

    /**
     * @return
     */
    @Override
    public String asString() {
        return _player.getUsername() + " Seat_" + _seatNumber + " " + _positionName;
    }

    public POSITION getPosition()  {
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
