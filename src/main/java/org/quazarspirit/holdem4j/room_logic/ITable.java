package org.quazarspirit.holdem4j.room_logic;

import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.holdem4j.game_logic.Pot;
import org.quazarspirit.holdem4j.game_logic.Round;
import org.quazarspirit.holdem4j.game_logic.card_pile.Board;
import org.quazarspirit.holdem4j.game_logic.card_pile.ICardPile;
import org.quazarspirit.holdem4j.player_logic.IPlayer;

import java.util.ArrayList;

public interface ITable {
    public Round getRound();

    public Dealer getDealer();

    public ICardPile getPocketCards(IPlayer player);

    public ArrayList<Position.NAME> getUsedPositions();

    public ArrayList<Position.NAME> getPlayingPositions();

    public int getPlayerCount();

    public int getMaxPlayerCount();

    public IPlayer getPlayerFromPosition(Position.NAME positionName);

    public Position.NAME getPositionFromPlayer(IPlayer concretePlayer);

    public Board getBoard();

    public Game getGame();

    public Pot getPot();
}
