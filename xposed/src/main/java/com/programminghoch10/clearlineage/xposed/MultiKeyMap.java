package com.programminghoch10.clearlineage.xposed;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultiKeyMap<K1, K2, V> extends HashMap<Integer, V> {
    Map<Integer, V> internalMap = new HashMap<>();

    private int getHash(Object obj1, Object obj2) {
        return obj1.hashCode() ^ obj2.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public boolean containsKey(K1 k1, K2 k2) {
        return super.containsKey(getHash(k1, k2));
    }

    public V get(K1 k1, K2 k2) {
        return super.get(getHash(k1, k2));
    }

    public V put(K1 k1, K2 k2, V value) {
        return super.put(getHash(k1,k2), value);
    }

    /* collides with a function from HashMap
    public V remove(K1 k1, K2 k2) {
        return super.remove(getHash(k1,k2));
    }
    */

    @Override
    public void putAll(Map<? extends Integer, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        super.clear();
    }

    @Override
    public Set<Integer> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<Integer, V>> entrySet() {
        throw new UnsupportedOperationException();
    }
}