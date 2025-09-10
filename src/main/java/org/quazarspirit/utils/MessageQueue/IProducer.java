package org.quazarspirit.Utils.MessageQueue;

import java.net.URI;
import java.net.URISyntaxException;

public interface IProducer {
    public void sendEvent(String json, URI uri) throws URISyntaxException;
}
