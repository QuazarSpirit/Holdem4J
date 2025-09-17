package org.quazarspirit.holdem4j.RoomLogic.Table;

import org.quazarspirit.holdem4j.CardPile.Board;
import org.quazarspirit.holdem4j.CardPile.ICardPile;
import org.quazarspirit.holdem4j.GameLogic.BettingRound;
import org.quazarspirit.holdem4j.GameLogic.Game;
import org.quazarspirit.holdem4j.GameLogic.ChipPile.Pot;
import org.quazarspirit.holdem4j.PlayerLogic.Player.IPlayer;
import org.quazarspirit.holdem4j.RoomLogic.Dealer;
import org.quazarspirit.holdem4j.RoomLogic.PositionEnum;

import java.util.ArrayList;
import java.util.UUID;

public interface ITable {
    public BettingRound getRound();

    public Dealer getDealer();

    public ICardPile getPocketCards(IPlayer player);

    public ArrayList<PositionEnum> getUsedPositions();

    public ArrayList<PositionEnum> getPlayingPositions();

    public int getPlayerCount();

    public int getMaxPlayerCount();

    public IPlayer getPlayerFromPosition(PositionEnum positionName);

    public PositionEnum getPositionFromPlayer(IPlayer concretePlayer);

    public Board getBoard();

    public Game getGame();

    public Pot getPot();

    public boolean addPlayer(IPlayer player);

    public UUID getUuid();
}
