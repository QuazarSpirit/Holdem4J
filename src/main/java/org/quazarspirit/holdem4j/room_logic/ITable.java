package org.quazarspirit.holdem4j.room_logic;

import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.holdem4j.game_logic.chip_pile.Pot;
import org.quazarspirit.holdem4j.game_logic.BettingRound;
import org.quazarspirit.holdem4j.game_logic.card_pile.Board;
import org.quazarspirit.holdem4j.game_logic.card_pile.ICardPile;
import org.quazarspirit.holdem4j.player_logic.player.IPlayer;

import java.util.ArrayList;

public interface ITable {
    public BettingRound getRound();

    public Dealer getDealer();

    public ICardPile getPocketCards(IPlayer player);

    public ArrayList<POSITION> getUsedPositions();

    public ArrayList<POSITION> getPlayingPositions();

    public int getPlayerCount();

    public int getMaxPlayerCount();

    public IPlayer getPlayerFromPosition(POSITION positionName);

    public POSITION getPositionFromPlayer(IPlayer concretePlayer);

    public Board getBoard();

    public Game getGame();

    public Pot getPot();
}
