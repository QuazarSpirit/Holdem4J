package org.quazarspirit.holdem4j.RoomLogic.Lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.JSONObject;
import org.quazarspirit.Utils.Utils;
import org.quazarspirit.holdem4j.GameLogic.Game;
import org.quazarspirit.holdem4j.PlayerLogic.Player.IPlayer;
import org.quazarspirit.holdem4j.PlayerLogic.Player.RealPlayer;
import org.quazarspirit.holdem4j.RoomLogic.ITable;
import org.quazarspirit.holdem4j.RoomLogic.Table;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class LobbyServer implements ILobby {
    final private HashMap<Game, ArrayList<ITable>> _tables = new HashMap<>();

    private HashMap<UUID, IPlayer> _players = new HashMap<UUID, IPlayer>();

    public LobbyServer(int port) {
        main(port);
    }

    public void main(int port) {
        Javalin app = Javalin.create().start(port);

        app.exception(Exception.class, (e, ctx) -> {
            Utils.Log("Erreur serveur : " + e); // Log complet avec stacktrace
            ctx.status(500).json(Map.of("error", "Erreur interne du serveur"));
        });

        app.get("/", ctx -> ctx.result("Hello there !"));
        app.post("/register", ctx -> registerPlayer(ctx));

        app.get("/get_player_data/{uuid}", ctx -> {
            String uuid = ctx.pathParam("uuid");
            ctx.json(fetchPlayer(UUID.fromString(uuid)));
        });

        app.get("/get_games", ctx -> {
            System.out.println(getGames());
            ctx.json(getGames());
        });

        app.post("/data", ctx -> {
            String body = ctx.body();
            ctx.result("Données reçues : " + body);
        });
    }

    /**
     * Basing player registration, no auth, nothing
     * 
     * @param context
     */
    private void registerPlayer(Context context) {
        String body = context.body();
        System.out.println(body);

        try {
            JSONObject json = new JSONObject(body);
            String username = (String) json.get("username");

            IPlayer player = new RealPlayer(UUID.randomUUID(), username);
            _players.put(player.getUUID(), player);
            context.json(player);
        } catch (ClassCastException e) {
            context.status(403);
            context.result("Your username needs to be a string !");
        } catch (Exception e) {
            System.err.println(e);
            context.status(403);
            context.result("You need to specify a username !");
        }
    }

    private IPlayer fetchPlayer(UUID uuid) {
        return _players.get(uuid);
    }

    @Override
    public void addGame(Game game) {
        System.out.println("Adding game to lobby");
        _tables.put(game, null);
    }

    @Override
    public void joinGame(IPlayer player, Game game) {
        // There is no tables registered for a game by default
        if (_tables.get(game) == null) {
            ITable table = new Table(game);
            _tables.put(game, new ArrayList<ITable>());
            addTableForGame(game, table);
        }

        // Check if there is a table available
    }

    private void addTableForGame(Game game, ITable table) {
        _tables.get(game).add(table);
    }

    @Override
    public Set<Game> getGames() {
        System.out.println(_tables.keySet());
        return _tables.keySet();
    }
}
