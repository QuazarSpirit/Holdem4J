package org.quazarspirit.Utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class ImmutableKV<K, V> extends AbstractMap.SimpleImmutableEntry<K, V> {
    public ImmutableKV(K key, V value) {
        super(key, value);
    }

    public static List<Object> getKeys(List<AbstractMap.SimpleImmutableEntry<Object, Object>> a) {
        return a.stream().map(AbstractMap.SimpleImmutableEntry::getKey).toList();
    }

    public static List<Object> getValues(ArrayList<ImmutableKV<Object, Object>> a) {
        return a.stream().map(AbstractMap.SimpleImmutableEntry::getValue).toList();
    }
}
