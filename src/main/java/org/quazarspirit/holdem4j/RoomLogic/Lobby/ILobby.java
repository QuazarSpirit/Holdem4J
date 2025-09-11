package org.quazarspirit.holdem4j.RoomLogic.Lobby;

import java.util.ArrayList;

import org.quazarspirit.holdem4j.GameLogic.Game;
import org.quazarspirit.holdem4j.PlayerLogic.Player.IPlayer;

public interface ILobby {
    /**
     * Add specified game to _games if not exists and create
     * new ArrayList of Tables for it in _gameToTables.
     * 
     * @param game Game structure to add to _games list and _gameToTables
     */
    public void addGame(Game game);

    /**
     * Add player to table with specified game
     * 
     * @param player Player to add on table
     * @param game   Game to lookup on _gameToTables hashmap
     */
    public void joinGame(IPlayer player, Game game);

    /**
     * @return List of available games
     */
    public ArrayList<Game> getGames();
}
