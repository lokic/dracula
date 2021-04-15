package com.github.lokic.dracula.eventbus.publisher;


import com.github.lokic.dracula.event.Event;

public interface ForwardingPublisher<E extends Event> extends Publisher<E> {

    Publisher<E> delegatePublisher();

    @Override
    default void publish(E event) {
        delegatePublisher().publish(event);
    }
}
