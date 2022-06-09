package org.quazarspirit.utils;

import java.util.AbstractMap;
import java.util.Map;

public class KV<K, V> {
    private K _key;
    private V _value;

    public KV() {}

    public KV(K key, V value) {
        _key = key;
        _value = value;
    }

    public void setKey(K key) {
        _key = key;
    }

    public K getKey() {
        return _key;
    }

    public void setValue(V value) {
        _value = value;
    }

    public V getValue() {
        return _value;
    }
}
