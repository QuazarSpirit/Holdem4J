package org.quazarspirit.Utils.PubSub;

import org.json.JSONObject;

public class Event {
    final public JSONObject data;
    final public IPublisher source;
    protected Object _type;

    public Event(IPublisher sourceArg, JSONObject dataArg) {
        source = sourceArg;
        data = dataArg;

        if (dataArg.has("type")) {
            _type = dataArg.get("type");
        }
    }

    public Event(IPublisher sourceArg, JSONObject dataArg, IEventType typeArg) {
        this(sourceArg, dataArg);
        _type = typeArg;
    }

    public Object getType() {
        return _type;
    }
}
