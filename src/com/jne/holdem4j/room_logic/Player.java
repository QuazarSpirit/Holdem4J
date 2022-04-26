package com.jne.holdem4j.room_logic;

import com.jne.holdem4j.room_logic.Table;

import java.util.ArrayList;
import java.util.UUID;

public class Player {
    protected UUID _uuid;
    protected ArrayList<Table> _tables = new ArrayList<Table>();

    public Player(UUID uuid ) {
        this._uuid = uuid;
    }

    public UUID getUUID() {
        return this._uuid;
    }

}
