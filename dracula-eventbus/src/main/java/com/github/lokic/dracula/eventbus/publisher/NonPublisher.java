package com.github.lokic.dracula.eventbus.publisher;


import com.github.lokic.dracula.event.Event;

public interface NonPublisher<E extends Event> extends Publisher<E> {

    @Override
    default void publish(E event) {

    }
}
