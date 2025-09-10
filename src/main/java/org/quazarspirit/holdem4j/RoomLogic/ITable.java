package org.quazarspirit.holdem4j.RoomLogic;

import org.quazarspirit.holdem4j.GameLogic.BettingRound;
import org.quazarspirit.holdem4j.GameLogic.Game;
import org.quazarspirit.holdem4j.GameLogic.CardPile.Board;
import org.quazarspirit.holdem4j.GameLogic.CardPile.ICardPile;
import org.quazarspirit.holdem4j.GameLogic.ChipPile.Pot;
import org.quazarspirit.holdem4j.PlayerLogic.Player.IPlayer;

import java.util.ArrayList;

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
}
