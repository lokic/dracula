package com.github.lokic.dracula.eventbus.transaction;

import com.github.lokic.javaplus.Types;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class EventKeyParser {

    private final Map<?, ?> mapping = new ConcurrentHashMap<>();

    private static final Function<?, String> DEFAULT_PARSER = o -> "dummy";

    @SuppressWarnings("unchecked")
    private <T> Map<Class<T>, Function<T, String>> getMapping() {
        return (Map<Class<T>, Function<T, String>>) mapping;
    }

    public <T> void bind(Class<T> clazz, Function<T, String> function) {
        this.<T>getMapping().putIfAbsent(clazz, function);
    }

    public <T> String parseEventKey(T obj) {
        return this.<T>getMapping()
                .getOrDefault(Types.getClass(obj), defaultParser())
                .apply(obj);
    }

    @SuppressWarnings("unchecked")
    private static <T> Function<T, String> defaultParser() {
        return (Function<T, String>) DEFAULT_PARSER;
    }
}
