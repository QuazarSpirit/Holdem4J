package org.quazarspirit.Utils.MessageQueue;

public interface IConsumer {
    void receiveEvent(String json);
}
