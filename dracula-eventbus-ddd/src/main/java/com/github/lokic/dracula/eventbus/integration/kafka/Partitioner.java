package com.github.lokic.dracula.eventbus.integration.kafka;

@FunctionalInterface
public interface Partitioner<E> {

    String key(E event);

    static <E> Partitioner<E> dummy() {
        return evt -> "dummy";
    }


}
