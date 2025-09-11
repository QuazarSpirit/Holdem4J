package org.quazarspirit.Utils;

import java.util.*;

import org.quazarspirit.holdem4j.GameLogic.Game;
import org.quazarspirit.holdem4j.GameLogic.GameVariant;

public class GameConfigLoader {

    private Map<String, Object> config;

    private final Map<String, Class<? extends Enum<?>>> enumMapping = Map.of(
            "betStructure", Game.BetStructureEnum.class,
            "gameFormat", Game.FormatEnum.class);

    private final Map<String, Enum<?>> parsedValues = new HashMap<>();

    public GameConfigLoader(Map<String, Object> config) {
        this.config = config;
        parseEnums();
        init();
    }

    private GameVariant _gameVariant;

    private void init() {
        _gameVariant = this.createVariant();
    }

    public GameVariant getVariant() {
        return _gameVariant;
    }

    private GameVariant createVariant() {
        // We need to get those keys from config:
        // pocket_card_size, board_card_size,
        // card_ranks, card_colors, card_ranking
        int pocketCardSize = (int) this.config.get("pocket_card_size");
        int boardCardSize = (int) this.config.get("board_card_size");
        String cardRanks = (String) this.config.get("card_ranks");
        String cardColors = (String) this.config.get("card_colors");
        List<String> cardRanking = (List<String>) this.config.get("card_ranking");

        GameVariant variant = new GameVariant.Builder()
                .boardCardSize(boardCardSize)
                .pocketCardSize(pocketCardSize)
                .cardRanks(cardRanks)
                .cardColors(cardColors)
                .build();

        return variant;
    }

    /**
     * Generic method to cast a value to his enum counterpart
     * 
     * @param <E>
     * @param enumClass
     * @param value
     * @return
     */
    private <E extends Enum<E>> E parseEnum(Class<? extends Enum<?>> enumClass, String value) {
        try {
            return Enum.valueOf((Class<E>) enumClass, value);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid value for " + enumClass.getSimpleName() + ": " + value);
            return null;
        }
    }

    // Associe chaque clé à sa valeur d'enum
    private void parseEnums() {
        for (Map.Entry<String, Class<? extends Enum<?>>> entry : enumMapping.entrySet()) {
            String key = entry.getKey();
            String rawValue = (String) config.get(key);
            if (rawValue != null) {
                Enum<?> enumValue = parseEnum(entry.getValue(), rawValue);
                if (enumValue != null) {
                    parsedValues.put(key, enumValue);
                }
            }
        }
    }

    // Accès typé aux valeurs
    public <E extends Enum<E>> E getEnumValue(String key, Class<E> enumClass) {
        Enum<?> value = parsedValues.get(key);
        if (value != null && enumClass.isInstance(value)) {
            return enumClass.cast(value);
        }
        return null;
    }

    public Collection<Enum<?>> getValues() {
        return parsedValues.values();
    }
}