package org.quazarspirit.utils.publisher_subscriber_pattern;

import org.json.JSONObject;

import java.util.ArrayList;

public class Publisher implements IPublisher {
    final private ArrayList<ISubscriber> _subscribers = new ArrayList<>();

    final private IPublisher _source;

    public Publisher() {
        _source = this;
    }

    public Publisher(IPublisher source) {
        _source = source;
    }

    /**
     * @param subscriber Subscriber object to add
     */
    @Override
    public void addSubscriber(ISubscriber subscriber) {
        if (! _subscribers.contains(subscriber)) {
            _subscribers.add(subscriber);
        }
    }

    /**
     * @param subscriber Subscriber object to remove
     */
    @Override
    public void removeSubscriber(ISubscriber subscriber) {
        _subscribers.remove(subscriber);
    }

    /**
     * @param jsonObject Data to be sent to all subscribers
     */
    @Override
    public void publish(JSONObject jsonObject) {
        Event event = new Event(this._source, jsonObject);
        for(ISubscriber subscriber: _subscribers) {
            subscriber.update(event);
        }
    }
}
