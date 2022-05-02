package org.quazarspirit.holdem4j.room_logic.player_logic;

import org.quazarspirit.holdem4j.room_logic.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Player implements IPlayer {
    private final UUID _uuid;
    protected final String username;
    protected ArrayList<Table> _tables = new ArrayList<Table>();

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
}
