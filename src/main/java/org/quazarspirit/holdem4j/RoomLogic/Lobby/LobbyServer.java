package org.quazarspirit.holdem4j.RoomLogic.Lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;
import org.quazarspirit.holdem4j.GameLogic.Game;
import org.quazarspirit.holdem4j.PlayerLogic.Player.IPlayer;
import org.quazarspirit.holdem4j.PlayerLogic.Player.RealPlayer;
import org.quazarspirit.holdem4j.RoomLogic.Table.ITable;
import org.quazarspirit.holdem4j.RoomLogic.Table.Table;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class LobbyServer implements ILobby {
    final private HashMap<UUID, Game> _games = new HashMap<>();
    final private HashMap<Game, ArrayList<ITable>> _tables = new HashMap<>();

    private HashMap<UUID, IPlayer> _players = new HashMap<UUID, IPlayer>();

    public LobbyServer(int port) {
        main(port);
    }

    public void main(int port) {
        Javalin app = Javalin.create().start(port);

        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "Internal server error"));
        });

        app.get("/", ctx -> ctx.result("Hello there !"));
        app.post("/register", ctx -> registerPlayer(ctx));

        app.get("/get_player_data/{uuid}", ctx -> {
            ctx.json(fetchPlayer(UUID.fromString(ctx.pathParam("uuid"))));
        });

        app.get("/get_games", ctx -> {
            ctx.json(getGames());
        });

        app.post("/join_game", ctx -> {
            joinGameFromRequest(ctx);
        });
    }

    /**
     * Basing player registration, no auth, nothing
     * 
     * @param ctx
     */
    private void registerPlayer(Context ctx) {
        String body = ctx.body();

        try {
            JSONObject json = new JSONObject(body);
            String username = (String) json.get("username");

            IPlayer player = new RealPlayer(UUID.randomUUID(), username);
            _players.put(player.getUUID(), player);
            ctx.json(player);
        } catch (ClassCastException e) {
            ctx.status(403);
            ctx.result("Your username needs to be a string !");
        } catch (Exception e) {
            System.err.println(e);
            ctx.status(403);
            ctx.result("You need to specify a username !");
        }
    }

    private void joinGameFromRequest(Context ctx) {
        String body = ctx.body();

        try {
            JSONObject json = new JSONObject(body);
            String playerUuid = (String) json.get("player_uuid");
            IPlayer player = fetchPlayer(UUID.fromString(playerUuid));
            if (player == null) {
                ctx.json(Map.of("error", "Player not found"));
                return;
            }

            String gameUuid = (String) json.get("game_uuid");
            Game game = _games.get(UUID.fromString(gameUuid));
            if (game == null) {
                ctx.json(Map.of("error", "Game not found"));
                return;
            }

            ctx.result("Joined game : " + game);
            joinGame(player, game);
        } catch (ClassCastException e) {
            ctx.status(500).json(Map.of("error", "Can't join games with specified params"));
        }
    }

    private IPlayer fetchPlayer(UUID uuid) {
        return _players.get(uuid);
    }

    @Override
    public void addGame(Game game) {
        System.out.println("Adding game to lobby");
        _games.put(UUID.randomUUID(), game);
        _tables.put(game, null);
    }

    @Override
    public void joinGame(IPlayer player, Game game) {
        // There is no tables registered for a game by default
        if (_tables.get(game) == null) {
            _tables.put(game, new ArrayList<ITable>());
            createNewTableForGame(game);
        }

        // Now we know there is a table
        ArrayList<ITable> tables = _tables.get(game);

        // Table has the responsability to handle player count, we can just try to join
        // every table and stop we addPlayer is true
        // If we dont have any table available, create a new one
        boolean playerJoined = false;
        for (ITable table : tables) {
            if (table.addPlayer(player)) {
                playerJoined = true;
                break;
            }
        }

        if (!playerJoined) {
            createNewTableForGame(game).addPlayer(player);
        }
    }

    private ITable createNewTableForGame(Game game) {
        ITable table = new Table(game);
        _tables.get(game).add(table);
        return table;
    }

    @Override
    public HashMap<UUID, Game> getGames() {
        return _games;
    }
}
