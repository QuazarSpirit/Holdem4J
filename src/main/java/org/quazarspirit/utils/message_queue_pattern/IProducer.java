package org.quazarspirit.utils.message_queue_pattern;

import java.net.URI;
import java.net.URISyntaxException;

public interface IProducer {
    public void sendEvent(String json, URI uri) throws URISyntaxException;
}
