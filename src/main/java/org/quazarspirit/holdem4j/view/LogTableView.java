package org.quazarspirit.holdem4j.view;

import org.quazarspirit.holdem4j.game_logic.BettingRound;
import org.quazarspirit.holdem4j.player_logic.player.IPlayer;
import org.quazarspirit.holdem4j.player_logic.enums.PLAYER_INTENT;
import org.quazarspirit.holdem4j.room_logic.ITable;
import org.quazarspirit.holdem4j.room_logic.POSITION;
import org.quazarspirit.holdem4j.room_logic.PositionHandler;
import org.quazarspirit.utils.Utils;
import org.quazarspirit.utils.publisher_subscriber_pattern.Event;

import java.util.ArrayList;
import java.util.Iterator;

public class LogTableView implements ITableView{
    /**
     * @param event Event to be logged
     */
    @Override
    public void update(Event event) {
        // Utils.Log("Source: " + event.source + "\n data: " + event.data);

        ITable table = (ITable) event.source;
        Utils.Log("Event: " + event.data.get("type"), Utils.LOG_LEVEL.DEBUG);
        Object eventDataType = event.data.get("type");
        if (eventDataType == PLAYER_INTENT.JOIN || eventDataType == PLAYER_INTENT.LEAVE) {
            //Utils.Log("Game: " + table.getGame().asString() +
            //                   " Player count: " + table.getPlayerCount());
        } else if (eventDataType == BettingRound.EVENT.NEXT) {
            if(table.getRound().getPhase() != BettingRound.PHASE.STASIS) {

                ArrayList<POSITION> playingPosition = table.getUsedPositions();

                for(Iterator<POSITION> iterator = playingPosition.iterator(); iterator.hasNext();) {
                    POSITION positionName = iterator.next();
                    IPlayer player = table.getPlayerFromPosition(positionName);
                    Utils.Log("PositionHandler: " + positionName +
                            " " + table.getPocketCards(player).asString());
                }

            }
        }
    }
}
