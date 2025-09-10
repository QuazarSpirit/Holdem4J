package org.quazarspirit.holdem4j.View;

import org.quazarspirit.Utils.Utils;
import org.quazarspirit.Utils.PubSub.Event;
import org.quazarspirit.holdem4j.GameLogic.BettingRound;
import org.quazarspirit.holdem4j.PlayerLogic.PlayerIntentEnum;
import org.quazarspirit.holdem4j.PlayerLogic.Player.IPlayer;
import org.quazarspirit.holdem4j.RoomLogic.ITable;
import org.quazarspirit.holdem4j.RoomLogic.PositionEnum;
import org.quazarspirit.holdem4j.RoomLogic.PositionHandler;

import java.util.ArrayList;
import java.util.Iterator;

public class LogTableView implements ITableView {
    /**
     * @param event Event to be logged
     */
    @Override
    public void update(Event event) {
        // Utils.Log("Source: " + event.source + "\n data: " + event.data);

        ITable table = (ITable) event.source;
        Utils.Log("Event: " + event.data.get("type"), Utils.LogLevelEnum.DEBUG);
        Object eventDataType = event.data.get("type");
        if (eventDataType == PlayerIntentEnum.JOIN || eventDataType == PlayerIntentEnum.LEAVE) {
            // Utils.Log("Game: " + table.getGame().asString() +
            // " Player count: " + table.getPlayerCount());
        } else if (eventDataType == BettingRound.EventEnum.NEXT) {
            if (table.getRound().getPhase() != BettingRound.PhaseEnum.STASIS) {

                ArrayList<PositionEnum> playingPosition = table.getUsedPositions();

                for (Iterator<PositionEnum> iterator = playingPosition.iterator(); iterator.hasNext();) {
                    PositionEnum positionName = iterator.next();
                    IPlayer player = table.getPlayerFromPosition(positionName);
                    Utils.Log("PositionHandler: " + positionName +
                            " " + table.getPocketCards(player).asString());
                }

            }
        }
    }
}
