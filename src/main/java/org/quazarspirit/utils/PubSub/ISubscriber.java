package org.quazarspirit.Utils.PubSub;

import java.util.function.Consumer;

public interface ISubscriber {
    void update(Event event);

    public void setOnMessage(Consumer<Event> onMessageCallback);
}
