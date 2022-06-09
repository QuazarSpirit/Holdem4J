package org.quazarspirit.holdem4j;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.http.HttpClient;

public class NetworkLogic extends Thread{
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    private int _port;

    public NetworkLogic(int port) throws SocketException {
        _port = port;
        socket = new DatagramSocket(_port);
    }

    public void sendPacket(DatagramPacket packet) throws IOException {
        socket.send(packet);

    }

    public void run() {
        System.out.println("UDP Server is listening on port: " + _port);
        running = true;

        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, packet.getLength(), address, port);
            String received = new String(packet.getData(), packet.getOffset(), packet.getLength());

            System.out.println(received);

            /*
            try {
                socket.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }*/
        }
        socket.close();
    }

    public static void main(String[] args) throws SocketException {
        Dotenv dotenv = Dotenv.configure().load();
        dotenv.entries().forEach(e -> {
            System.setProperty(e.getKey(), e.getValue());
        });

        String port = System.getProperty("H4J_PORT");
        NetworkLogic netLogic = new NetworkLogic(Integer.parseInt(port));
        netLogic.start();
    }
}
