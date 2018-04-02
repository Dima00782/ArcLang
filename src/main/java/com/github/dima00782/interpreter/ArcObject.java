package com.github.dima00782.interpreter;

import javafx.util.Pair;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ArcObject {
    private AtomicInteger refCount = new AtomicInteger(1);
    private ConcurrentHashMap<String, Pair<ArcObject, Boolean>> fields = new ConcurrentHashMap<>();

    public ArcObject getField(String fieldName) {
        Pair<ArcObject, Boolean> pair = fields.get(fieldName);
        if (pair == null) {
            return null;
        }
        return pair.getKey();
    }

    public void addFiled(String fieldName, ArcObject value, boolean isWeak) {
        fields.put(fieldName, new Pair<>(value, isWeak));
    }

    public void removeField(String name) {
        ArcObject objectToDelete = getField(name);
        fields.remove(name);
        ConcurrentHashMap<String, Pair<ArcObject, Boolean>> childMap = objectToDelete.fields;
        Set<String> namesSet = childMap.keySet();
        String[] names = namesSet.toArray(new String[namesSet.size()]);

        Arrays.stream(names).forEach(objectToDelete::removeField);
    }

    public boolean isWeak(String fieldName) {
        Pair<ArcObject, Boolean> pair = fields.get(fieldName);
        if (pair == null) {
            return true;
        }
        return pair.getValue();
    }

    public void incrementRefCount() {
        refCount.incrementAndGet();
    }

    @Override
    public String toString() {
        return "ArcObject{" +
                "refCount=" + refCount +
                ", fields=" + fields +
                '}';
    }

    public int decrement(String name) {
        return getField(name).refCount.decrementAndGet();
    }
}
