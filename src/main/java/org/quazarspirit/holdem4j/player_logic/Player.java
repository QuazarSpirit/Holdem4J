package org.quazarspirit.holdem4j.player_logic;

import org.quazarspirit.holdem4j.game_logic.Bet;
import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.holdem4j.room_logic.Table;
import org.quazarspirit.utils.KV;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

abstract class Player implements IPlayer {
    private final UUID _uuid;
    protected final String username;
    private final ArrayList<Table> _tables = new ArrayList<>();

    public Player(UUID uuid, String username) {
        this._uuid = uuid;
        this.username = username;
    }

    public UUID getUUID() {
        return this._uuid;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /**
     * @return
     */
    // TODO: Implements
    public KV<PLAYER_ACTION, Bet> queryAction(ArrayList<PLAYER_ACTION> allowedActions, Game gameArg) {
        return new KV<>(PLAYER_ACTION.FOLD, new Bet(gameArg));
    }

    /**
     * @param table
     */
    @Override
    public void addTable(Table table) {
        _tables.add(table);
    }

    /**
     * @param playerToCheck
     * @return
     */
    @Override
    public boolean equals(IPlayer playerToCheck) {
        return Objects.equals(this.username, playerToCheck.getUsername()) && this._uuid == playerToCheck.getUUID();
    }
}
