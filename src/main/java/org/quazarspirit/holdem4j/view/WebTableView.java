package org.quazarspirit.holdem4j.view;

import org.quazarspirit.utils.publisher_subscriber_pattern.Event;

public class WebTableView implements ITableView{
    /**
     * @param event Event to be logged
     */
    @Override
    public void update(Event event) {
        // Utils.Log("Source: " + event.source + "\n data: " + event.data);
            /*
        ITable table = (ITable) event.source;
        StringBuilder HTML_data = new StringBuilder("<html><body>");
        Utils.Log("Event: " + event.data.get("type"));
        Object eventDataType = event.data.get("type");
        if (eventDataType == PLAYER_INTENT.JOIN || eventDataType == PLAYER_INTENT.LEAVE) {
            //Utils.Log("Game: " + table.getGame().asString() +
            //                   " Player count: " + table.getPlayerCount());
            int player_count = table.getPlayerCount();
            HTML_data.append("<table>");
            HTML_data.append("<td>")
                     .append("<td>")  HTML_data;.append("<td>"); HTML_data.append("<td>"); HTML_data.append("<td>");
            HTML_data.append("<td>"); HTML_data.append("<td>");  HTML_data.append("<td>"); HTML_data.append("<td>"); HTML_data.append("<td>");
            HTML_data.append("</table>");
        } else if (eventDataType == BettingRound.EVENT.NEXT) {
            if(table.getRound().getPhase() != BettingRound.PHASE.STASIS) {

                ArrayList<PositionHandler.POSITION> playingPosition = table.getUsedPositions();

                for(Iterator<PositionHandler.POSITION> iterator = playingPosition.iterator(); iterator.hasNext();) {
                    PositionHandler.POSITION positionName = iterator.next();
                    IPlayer player = table.getPlayerFromPosition(positionName);
                    Utils.Log("PositionHandler: " + positionName +
                            " " + table.getPocketCards(player).asString());
                }

            }
        }

        HTML_data.append("</body></html>");

             */
    }
}
