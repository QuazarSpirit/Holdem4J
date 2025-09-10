package org.quazarspirit.holdem4j.RoomLogic.Lobby;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.quazarspirit.holdem4j.GameLogic.Game;
import org.quazarspirit.holdem4j.RoomLogic.Table;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class HttpLobby {
    private static HttpLobby instance;
    private static int sPort = 8080;
    final int port;
    final private HashMap<Game, ArrayList<Table>> _tables = new HashMap<>();

    public static HttpLobby getInstance() throws Exception {
        if (instance == null) {
            instance = new HttpLobby(sPort);
        }
        return instance;
    }

    public static void setPort(int port) throws Exception {
        if (instance != null) {
            throw new Exception("HttpLobby is already started");
        }

        sPort = port;
    }

    public static void main(String[] args) throws Exception {
        HttpLobby.setPort(2000);
        HttpLobby lobby = HttpLobby.getInstance();
        lobby.startHttpServer();
    }

    private HttpLobby(int port) throws Exception {
        // Needs to be a valid TCP/IP port
        if (port > 65535) {
            throw new Exception();
        }

        this.port = port;

        startHttpServer();
    }

    // Source:
    // https://stackoverflow.com/questions/3732109/simple-http-server-in-java-using-only-java-se-api
    private void startHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(this.port), 0);
        server.createContext("/register", new RegisterHandler());
        server.createContext("/player/join", new PlayerJoinTableHandler());
        server.createContext("/tables/join", new PlayerJoinLobbyHandler());
        server.setExecutor(null);
        server.start();
    }

    public void addTable(Table table) {
        ArrayList<Table> tables_for_game = _tables.get(table.getGame());
        if (tables_for_game == null) {
            // If no table registered for games
            // Create array and add table
            tables_for_game = new ArrayList<Table>();
            tables_for_game.add(table);
            _tables.put(table.getGame(), tables_for_game);
            return;
        }

        // Adding table to current array
        tables_for_game.add(table);

        // Replacing current array with updated one
        _tables.put(table.getGame(), tables_for_game);
        System.out.println("Successfully added table to registry");
    }

    /**
     * Returning first opened table for specified game.
     * 
     * @param game Specific game structure
     * @return Table or null
     */
    public Table getAvailableTable(Game game) {
        // Returning first table marked as available
        ArrayList<Table> tables_for_game = _tables.get(game);

        if (tables_for_game == null || tables_for_game.isEmpty()) {
            // Means that there is no table for that specific game
            // Maybe could return similar tables
            return null;
        }

        for (Table table : tables_for_game) {
            if (table.getIsOpened()) {
                return table;
            }
        }

        // If there was not any available returning null
        return null;
    }

    private static boolean validateJson(JSONObject obj) {
        try {
            obj.getString("player_type");
            obj.getString("bet_structure");
            obj.getString("variant");
            obj.getString("sub_variant");
            obj.getString("format");
            obj.getString("max_seats");
            obj.getInt("stack_size");
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    static Game getGameFromBody(InputStream stream) {
        JSONTokener to = new JSONTokener(stream);
        JSONObject json;
        try {
            json = new JSONObject(to);
        } catch (JSONException e) {
            System.out.println("Body not valid json object returning");
            return null;
        }

        // Should use auth system like JWT but not for now
        // Get body and parse as json.
        // It must contain all keys to create a table instance
        if (!validateJson(json)) {
            System.out.println("Json does not contains all necessary keys, returning");
            return null;
        }

        return new Game();
        // return Game.fromJson(json);
    }

    static class RegisterHandler implements HttpHandler {
        /**
         * @param exchange the exchange containing the request from the
         *                 client and used to send the response
         * @throws IOException .
         */
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // TODO: Implement

            Game game = getGameFromBody(exchange.getRequestBody());
            if (game == null) {
                exchange.sendResponseHeaders(403, -1);
                return;
            }

            Table table = new Table(game);

            // Save table in memory
            HttpLobby lobby;
            try {
                lobby = HttpLobby.getInstance();
            } catch (Exception e) {
                System.out.println("Exception");
                exchange.sendResponseHeaders(500, -1);
                return;
            }

            lobby.addTable(table);

            // Closing response
            exchange.sendResponseHeaders(200, -1);
        }

    }

    static class PlayerJoinTableHandler implements HttpHandler {

        /**
         * @param exchange the exchange containing the request from the
         *                 client and used to send the response
         */
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // TODO: Get player from player registry
            // If not already in table make it join

            // Player authentication
            // Player instantiation

            // Get game
            Game game = getGameFromBody(exchange.getRequestBody());
            if (game == null) {
                exchange.sendResponseHeaders(403, -1);
                return;
            }

            HttpLobby lobby;
            try {
                lobby = HttpLobby.getInstance();
            } catch (Exception e) {
                System.out.println("Exception");
                exchange.sendResponseHeaders(500, -1);
                return;
            }

            Table table = lobby.getAvailableTable(game);

            if (table == null) {
                System.out.println("No table available");
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            // TODO: Send info to connect to websocket
            // Maybe server ip ?
            exchange.sendResponseHeaders(200, -1);
        }
    }

    static class PlayerJoinLobbyHandler implements HttpHandler {

        /**
         * @param exchange the exchange containing the request from the
         *                 client and used to send the response
         */
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // TODO: Instantiate player
            // Add it to player registry
        }
    }

}
