package org.quazarspirit.holdem4j.room_logic.player_logic;

public class NullPlayer implements IPlayer {
    static final private NullPlayer _singleton = new NullPlayer();
    static final String NULL_USERNAME = "NULL_USERNAME";
    private NullPlayer() {}

    static public NullPlayer GetSingleton() {
        return _singleton;
    }

    @Override
    public String getUsername() {
        return NullPlayer.NULL_USERNAME;
    }
}
