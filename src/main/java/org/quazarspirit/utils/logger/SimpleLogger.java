package org.quazarspirit.Utils.Logger;

import org.quazarspirit.Utils.Utils;
import org.quazarspirit.Utils.PubSub.Event;

public class SimpleLogger implements ILogger {
    /**
     * @param message Object (stringified) to be logged
     */
    @Override
    public void log(Object message) {
        System.out.println(message);
    }

    /**
     * @param event We are only looking for LOG events to be logged
     */
    @Override
    public void update(Event event) {
        if (event.getType() != Utils.EventEnum.LOG) {
            return;
        }
        log(event.data.get("message"));
    }
}
