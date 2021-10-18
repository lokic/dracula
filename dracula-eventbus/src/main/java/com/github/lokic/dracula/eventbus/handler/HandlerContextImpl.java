package com.github.lokic.dracula.eventbus.handler;

import lombok.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;


public class HandlerContextImpl implements HandlerContext {

    private final Map<String, Object> attributes = new LinkedHashMap<>(0);

    @Override
    public void setAttribute(@NonNull String name, Object value) {
        if (value != null) {
            this.attributes.put(name, value);
        } else {
            removeAttribute(name);
        }
    }

    @Override
    public Object getAttribute(@NonNull String name) {
        return this.attributes.get(name);
    }

    @Override
    public Object removeAttribute(@NonNull String name) {
        return this.attributes.remove(name);
    }

    @Override
    public boolean hasAttribute(@NonNull String name) {
        return this.attributes.containsKey(name);
    }

    @Override
    public String[] attributeNames() {
        return this.attributes.keySet().toArray(new String[0]);
    }
}