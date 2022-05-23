package org.quazarspirit.holdem4j.room_logic.player_logic;
import org.quazarspirit.holdem4j.game_logic.Bet;
import org.quazarspirit.utils.ImmutableKV;

public interface IPlayer {
    String getUsername();
    ImmutableKV<PLAYER_ACTION, Bet> queryAction();
}
