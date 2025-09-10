package org.quazarspirit.Utils.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.quazarspirit.Utils.Utils;
import org.quazarspirit.Utils.PubSub.Event;
import org.quazarspirit.Utils.PubSub.ISubscriber;

public class TCPLogger extends Thread implements ILogger, ISubscriber {
    private static TCPLogger _singleton;
    private final int _wss_port;
    private boolean _available = false;
    private Socket _client;
    private ServerSocket _server;

    private TCPLogger() {
        _wss_port = (int) Utils.GetEnv("wss_port", 800);
    }

    public static TCPLogger GetSingleton() {
        if (_singleton == null) {
            _singleton = new TCPLogger();

            try {
                _singleton.start();
            } catch (IllegalThreadStateException e) {
                Utils.Log("Thread already started");
            }
        }
        return _singleton;
    }

    public static void main(String[] args) throws Exception {
        TCPLogger logger = TCPLogger.GetSingleton();
    }

    @Override
    public void run() {
        try {
            _singleton.startSocketServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSocketServer() throws IOException {
        System.out.println("Socket server started on port: " + _wss_port);
        _server = new ServerSocket(_wss_port);
        _server.setReuseAddress(true);

        // Waits for a client to connect
        _client = _server.accept();
        System.out.println("Client connected.");
        _available = true;
    }

    public void keepConnectionAlive() throws IOException {
        System.out.println("Connection alive");
        while (_client.isConnected()) {

            try {
                TimeUnit.SECONDS.sleep(3);
                _client.getOutputStream().write("PING\n".getBytes());
            } catch (SocketException e) {
                resetServer();
                break;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void resetServer() {
        try {
            _client.close();
            _server.close();
            /*
             * while (_client.isClosed() || !_server.isClosed()) {
             * System.out.println("Waiting");
             * TimeUnit.MILLISECONDS.sleep(300);
             * }
             */
            TimeUnit.MILLISECONDS.sleep(300);
            startSocketServer();
        } catch (IOException | InterruptedException e) {
            System.out.println("Couldn't restart server");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void log(Object message) {
        if (_available) {
            try {
                String str = message.toString() + "\n";
                _client.getOutputStream().write(str.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
                resetServer();
            }
        }
    }

    @Override
    public void update(Event event) {
        log(event.data.get("message"));
    }
}
