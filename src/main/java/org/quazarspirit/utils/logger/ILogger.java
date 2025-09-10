package org.quazarspirit.Utils.Logger;

import org.quazarspirit.Utils.PubSub.ISubscriber;

public interface ILogger extends ISubscriber {
    void log(Object message);
}
