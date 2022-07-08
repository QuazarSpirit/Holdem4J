package org.quazarspirit.utils.message_queue_pattern;

import com.sun.net.httpserver.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.logging.Logger;

// https://mkyong.com/Java/how-to-send-http-request-getpost-in-Java/
public class MessageQueue {
    private final int _port;
    private static Logger _logger = Logger.getLogger(MessageQueue.class.getName());
    private HttpServer _httpServer;
    private final LinkedList<String> _queue = new LinkedList<>();

    MessageQueue(int HttpPort) {
        _port = HttpPort;
    }

    private void createRoutes() {
        _httpServer.createContext("/messages/first", new getMessage());
        _httpServer.createContext("/messages/add", new pushMessage());
        _httpServer.createContext("/messages", new getAllMessages());
    }

    class getMessage implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = _queue.getFirst();
            exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    class getAllMessages implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            exchange.sendResponseHeaders(200, 0);//response code and length
            OutputStream os = exchange.getResponseBody();

            for (String s : _queue) {
                s += "\n";
                os.write(s.getBytes());
            }
            os.close();
        }
    }

    class pushMessage implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                InputStream is = exchange.getRequestBody();
                byte[] byteData = is.readAllBytes();
                is.close();

                // Instantly close response
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, -1);
                String data = new String(byteData);
                _queue.add(data);
            }
        }
    }

    public void init() {
        try {
            _httpServer = HttpServer.create();
            _httpServer.bind(new InetSocketAddress(_port), 0);
            createRoutes();
            _httpServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MessageQueue MQ = new MessageQueue(4000);
        MQ.init();
    }
}
