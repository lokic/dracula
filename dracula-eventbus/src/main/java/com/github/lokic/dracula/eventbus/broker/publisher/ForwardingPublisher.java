package com.github.lokic.dracula.eventbus.broker.publisher;


import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.Publisher;

public interface ForwardingPublisher<E extends Event> extends Publisher<E> {

    Publisher<E> getTargetPublisher();

    @Override
    default void publish(E event) {
        getTargetPublisher().publish(event);
    }
}
