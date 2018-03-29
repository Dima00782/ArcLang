package com.github.dima00782.interpreter;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ArcObject {
    private AtomicInteger refCount = new AtomicInteger(1);
    private ConcurrentHashMap<String, ArcObject> fields = new ConcurrentHashMap<>();

    public ArcObject getField(String fieldName) {
        return fields.get(fieldName);
    }

    public void addFiled(String fieldName, ArcObject value) {
        fields.put(fieldName, value);
    }

    public void removeField(String name) {
        ArcObject objectToDelete = fields.get(name);
        fields.remove(name);
        ConcurrentHashMap<String, ArcObject> childMap = objectToDelete.fields;
        Set<String> namesSet = childMap.keySet();
        String[] names = namesSet.toArray(new String[namesSet.size()]);

        Arrays.stream(names).forEach(objectToDelete::removeField);
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
        return refCount.decrementAndGet();
    }
}
