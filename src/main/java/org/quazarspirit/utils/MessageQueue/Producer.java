package org.quazarspirit.Utils.MessageQueue;

import org.json.JSONObject;
import org.quazarspirit.Utils.Utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Producer implements IProducer {
    private final HttpClient _httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    public void sendEvent(String data, URI uri) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .uri(uri)
                .setHeader("User-Agent", "Holdem4J Spectator") // add request header
                .header("Content-Type", "application/json")
                .build();

        try {
            HttpResponse<String> response = _httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // print status code
            Utils.Log(response.statusCode());

            // print response body
            Utils.Log(response.body());
        } catch (Exception e) {
            if (!e.getClass().getSimpleName().equals("ConnectException")) {
                e.printStackTrace();
            }
        }
    }

    public void sendEvent(JSONObject json, URI uri) {
        sendEvent(json.toString(), uri);
    }
}
