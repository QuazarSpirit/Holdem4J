package org.quazarspirit.utils.publisher_subscriber_pattern;

import org.json.JSONObject;

public class Event {
    final public JSONObject data;
    final public IPublisher source;
    protected Object _type;

    Event(IPublisher sourceArg, JSONObject dataArg) {
        source = sourceArg;
        data = dataArg;
        _type = dataArg.get("type");
    }

    Event(IPublisher sourceArg, JSONObject dataArg, IEventType typeArg) {
        this(sourceArg, dataArg);
        _type = typeArg;
    }

    public Object getType() {
        return _type;
    }
}
