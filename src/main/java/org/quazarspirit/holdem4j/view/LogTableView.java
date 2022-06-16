package org.quazarspirit.holdem4j.view;

import org.quazarspirit.holdem4j.game_logic.Round;
import org.quazarspirit.holdem4j.player_logic.IPlayer;
import org.quazarspirit.holdem4j.player_logic.PLAYER_INTENT;
import org.quazarspirit.holdem4j.room_logic.ITable;
import org.quazarspirit.holdem4j.room_logic.Position;
import org.quazarspirit.utils.publisher_subscriber_pattern.Event;

import java.util.ArrayList;
import java.util.Iterator;

public class LogTableView implements ITableView{
    /**
     * @param event Event to be logged
     */
    @Override
    public void update(Event event) {
        // System.out.println("Source: " + event.source + "\n data: " + event.data);

        ITable table = (ITable) event.source;
        System.out.println("Event: " + event.data.get("type"));
        Object eventDataType = event.data.get("type");
        if (eventDataType == PLAYER_INTENT.JOIN || eventDataType == PLAYER_INTENT.LEAVE) {
            System.out.println("Game: " + table.getGame().asString() +
                               " Player count: " + table.getPlayerCount());
        } else if (eventDataType == Round.EVENT.NEXT) {
            if(table.getRound().getRoundPhase() != Round.ROUND_PHASE.STASIS) {

                ArrayList<Position.NAME> playingPosition = table.getUsedPositions();

                for(Iterator<Position.NAME> iterator = playingPosition.iterator(); iterator.hasNext();) {
                    Position.NAME positionName = iterator.next();
                    IPlayer player = table.getPlayerFromPosition(positionName);
                    System.out.println("Position: " + positionName +
                            " " + table.getPocketCards(player).asString());
                }

            }
        }
    }
}
