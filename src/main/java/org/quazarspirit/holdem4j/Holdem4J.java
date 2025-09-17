package org.quazarspirit.holdem4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.quazarspirit.Utils.GameConfigLoader;
import org.quazarspirit.holdem4j.GameLogic.Game;
import org.quazarspirit.holdem4j.RoomLogic.Lobby.ILobby;
import org.quazarspirit.holdem4j.RoomLogic.Lobby.LobbyServer;
import org.yaml.snakeyaml.Yaml;

public class Holdem4J {
    public static void main(String[] args) throws URISyntaxException {
        String configPath = null;
        for (String arg : args) {
            if (arg.startsWith("--config=")) {
                configPath = arg.substring("--config=".length());
            }
        }

        if (configPath == null) {
            System.out.println("No config file provided using default TexasHoldemShortHanded.yaml");
            ClassLoader cl = Holdem4J.class.getClassLoader();
            configPath = cl.getResource("./GameVariants/TexasHoldemShortHanded.yaml").toURI().getRawPath();
        }

        // Load config file
        Map<String, Object> configObj = null;
        try (FileInputStream fis = new FileInputStream(configPath)) {
            Yaml config = new Yaml();
            configObj = config.load(fis);
        } catch (FileNotFoundException e) {
            System.err.println("File located at: " + configPath + " not found !");
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("There was an error while reading the config file : " + e.getMessage());
            System.exit(-2);
        }

        try {
            GameConfigLoader loader = new GameConfigLoader(configObj);
            Game game = new Game(loader);
            System.out.println("Game : " + game.asString());

            // Add game to lobby
            ILobby lobby = new LobbyServer(6000, game.getBrokerUrl());
            lobby.addGame(game);
        } catch (Exception e) {
            System.err.println("Fatal error during game setup: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
