package org.quazarspirit.utils.logger;

import org.quazarspirit.utils.publisher_subscriber_pattern.ISubscriber;

public interface ILogger extends ISubscriber {
    void log(Object message);
}
