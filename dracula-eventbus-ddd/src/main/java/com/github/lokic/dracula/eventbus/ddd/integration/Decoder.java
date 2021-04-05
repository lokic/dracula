package com.github.lokic.dracula.eventbus.ddd.integration;

@FunctionalInterface
public interface Decoder<T> {
    T decode(String s);
}
