package org.quazarspirit.utils;

import org.quazarspirit.holdem4j.game_logic.Card;

import java.util.AbstractMap;

public class ImmutableKV<K, V> extends AbstractMap.SimpleImmutableEntry<K, V>{
    public ImmutableKV(K key, V value) {
        super(key, value);
    }
}
