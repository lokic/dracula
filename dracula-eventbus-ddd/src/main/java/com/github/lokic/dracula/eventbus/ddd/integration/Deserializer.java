package com.github.lokic.dracula.eventbus.ddd.integration;

@FunctionalInterface
public interface Deserializer<T> {
    T deserialize(String s);
}
