package com.github.lokic.dracula.eventbus.integration.publisher;

@FunctionalInterface
public interface Serializer<T> {
    String serialize(T t);
}
