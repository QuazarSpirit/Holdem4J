package org.quazarspirit.utils.logger;

import org.quazarspirit.utils.Utils;
import org.quazarspirit.utils.publisher_subscriber_pattern.Event;

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
        if (event.getType() != Utils.EVENT.LOG) {return;}
        log(event.data.get("message"));
    }
}
