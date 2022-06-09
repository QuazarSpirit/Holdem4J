package org.quazarspirit.utils.publisher_subscriber_pattern;

import org.json.JSONObject;

public class Event {
    final public JSONObject data;
    final public IPublisher source;

    Event(IPublisher sourceArg, JSONObject dataArg) {
        source = sourceArg;
        data = dataArg;
    }
}
