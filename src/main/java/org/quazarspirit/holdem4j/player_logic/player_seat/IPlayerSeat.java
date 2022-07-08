package org.quazarspirit.holdem4j.player_logic.player_seat;

import org.quazarspirit.holdem4j.player_logic.player.IPlayer;
import org.quazarspirit.holdem4j.room_logic.POSITION;
import org.quazarspirit.holdem4j.room_logic.PositionHandler;

public interface IPlayerSeat {
    public POSITION getPosition();
    public IPlayer getPlayer();
    public int getSeatNumber();
    public void setPosition(POSITION positionName);

    public String asString();
}
