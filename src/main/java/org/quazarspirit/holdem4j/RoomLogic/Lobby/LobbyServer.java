package org.quazarspirit.holdem4j.RoomLogic.Lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;
import org.quazarspirit.holdem4j.GameLogic.Game;
import org.quazarspirit.holdem4j.PlayerLogic.Player.IPlayer;
import org.quazarspirit.holdem4j.PlayerLogic.Player.RealPlayer;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class LobbyServer implements ILobby {

    private HashMap<UUID, IPlayer> _players = new HashMap<UUID, IPlayer>();

    public LobbyServer(int port) {
        main(port);
    }

    public void main(int port) {
        Javalin app = Javalin.create().start(port);

        app.get("/", ctx -> ctx.result("Hello there !"));
        app.post("/register", ctx -> registerPlayer(ctx));

        app.get("/get_player_data/{uuid}", ctx -> {
            String uuid = ctx.pathParam("uuid");
            ctx.json(fetchPlayer(UUID.fromString(uuid)));
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addGame'");
    }

    @Override
    public void joinGame(IPlayer player, Game game) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'joinGame'");
    }

    @Override
    public ArrayList<Game> getGames() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getGames'");
    }
}
