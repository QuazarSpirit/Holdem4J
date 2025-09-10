package org.quazarspirit.Utils.PubSub;

import org.json.JSONObject;

public interface IPublisher {
    void addSubscriber(ISubscriber subscriber);

    void removeSubscriber(ISubscriber subscriber);

    void publish(JSONObject jsonObject);
}
