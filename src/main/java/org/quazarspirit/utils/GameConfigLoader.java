package org.quazarspirit.Utils;

import java.util.*;

import org.quazarspirit.holdem4j.GameLogic.Game;

public class GameConfigLoader {

    private Properties config = new Properties();

    private final Map<String, Class<? extends Enum<?>>> enumMapping = Map.of(
            "betStructure", Game.BET_STRUCTURE.class,
            "gameVariant", Game.VARIANT.class,
            "gameFormat", Game.FORMAT.class);

    private final Map<String, Enum<?>> parsedValues = new HashMap<>();

    public GameConfigLoader(Properties config) {
        this.config = config;
        parseEnums();
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
            String rawValue = config.getProperty(key);
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