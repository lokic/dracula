package com.github.lokic.dracula.eventbus.ddd.integration;

@FunctionalInterface
public interface Serializer<T> {
    String serialize(T t);
}
