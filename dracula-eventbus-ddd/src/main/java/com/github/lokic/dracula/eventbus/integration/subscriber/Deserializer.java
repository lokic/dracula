package com.github.lokic.dracula.eventbus.integration.subscriber;

@FunctionalInterface
public interface Deserializer<T> {
    T deserialize(String s);
}
