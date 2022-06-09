package org.quazarspirit.utils.message_queue_pattern;

public interface IConsumer {
    void receiveEvent(String json);
}
