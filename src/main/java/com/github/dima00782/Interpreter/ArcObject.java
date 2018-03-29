package com.github.dima00782.Interpreter;

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
}
