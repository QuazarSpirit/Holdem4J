package org.quazarspirit.holdem4j.PlayerLogic.PlayerSeat;

import org.quazarspirit.holdem4j.PlayerLogic.Player.IPlayer;
import org.quazarspirit.holdem4j.RoomLogic.PositionEnum;
import org.quazarspirit.holdem4j.RoomLogic.PositionHandler;

public interface IPlayerSeat {
    public PositionEnum getPosition();

    public IPlayer getPlayer();

    public int getSeatNumber();

    public void setPosition(PositionEnum positionName);

    public String asString();
}
