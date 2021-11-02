package com.programminghoch10.clearlineage.xposed;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MultiKeyMap<V> {
    Map<Integer, V> internalMap = new HashMap<>();

    private int getHash(Object... obj) {
        return Objects.hash(obj);
    }

    public boolean isEmpty() {
        return internalMap.isEmpty();
    }

    public boolean containsKey(Object... obj) {
        return internalMap.containsKey(getHash(obj));
    }

    public V get(Object... obj) {
        return internalMap.get(getHash(obj));
    }

    public V put(V value, Object... obj) {
        return internalMap.put(getHash(obj), value);
    }

    public V remove(Object... obj) {
        return internalMap.remove(getHash(obj));
    }

    public void clear() {
        internalMap.clear();
    }

}
