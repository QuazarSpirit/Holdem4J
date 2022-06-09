package org.quazarspirit.holdem4j.player_logic;

import org.quazarspirit.holdem4j.game_logic.Bet;
import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.holdem4j.room_logic.Table;
import org.quazarspirit.utils.ImmutableKV;
import org.quazarspirit.utils.KV;

import java.util.ArrayList;
import java.util.UUID;

public class NullPlayer implements IPlayer {
    static final private NullPlayer _singleton = new NullPlayer();
    static final String NULL_USERNAME = "NULL_USERNAME";
    static final private UUID _uuid =  UUID.fromString("00000000-0000-1000-2000-000000000000");
    private NullPlayer() {}

    static public NullPlayer GetSingleton() {
        return _singleton;
    }

    @Override
    public String getUsername() {
        return NullPlayer.NULL_USERNAME;
    }

    /**
     * @return
     */
    @Override
    public KV<PLAYER_ACTION, Bet> queryAction(ArrayList<PLAYER_ACTION> allowedActions, Game gameArg) {
        return new KV<>(PLAYER_ACTION.FOLD, new Bet(gameArg));
    }

    /**
     * @param table
     */
    @Override
    public void addTable(Table table) {
        return;
    }

    /**
     * @param playerToCheck
     * @return
     */
    @Override
    public boolean equals(IPlayer playerToCheck) {
        return this == playerToCheck;
    }

    /**
     * @return
     */
    @Override
    public UUID getUUID() {
        return _uuid;
    }
}
