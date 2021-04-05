package com.github.lokic.dracula.eventbus.ddd.integration;

@FunctionalInterface
public interface Encoder<T> {
    String encode(T t);
}
