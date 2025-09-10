package org.quazarspirit.holdem4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.quazarspirit.Utils.GameConfigLoader;
import org.quazarspirit.holdem4j.GameLogic.Game;
import org.quazarspirit.holdem4j.RoomLogic.Lobby.Lobby;

public class Holdem4J {
    public static void main(String[] args) {
        // Program entry point
        Properties config = new Properties();

        // default path
        String configPath = "config.properties";
        for (String arg : args) {
            if (arg.startsWith("--config=")) {
                configPath = arg.substring("--config=".length());
            }
        }

        // Load config file
        try (FileInputStream fis = new FileInputStream(configPath)) {
            config.load(fis);
        } catch (FileNotFoundException e) {
            System.out.println("WARNING: File located at: " + configPath
                    + " not found ! It might be normal if you are using CLI args.");
        } catch (IOException e) {
            System.err.println("There was an error while reading the config file : " + e.getMessage());
        }

        // CLI args override
        for (String arg : args) {
            if (arg.startsWith("--")) {
                String[] parts = arg.substring(2).split("=", 2);
                if (parts.length == 2) {
                    config.setProperty(parts[0], parts[1]);
                }
            }
        }

        try {
            GameConfigLoader loader = new GameConfigLoader(config);
            Game game = new Game(loader);
            System.out.println("Game : " + game.asString());

            // Add game to lobby
            Lobby lobby = Lobby.getSingleton();
            lobby.addGame(game);

        } catch (Exception e) {
            System.err.println("Fatal error during game setup: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
