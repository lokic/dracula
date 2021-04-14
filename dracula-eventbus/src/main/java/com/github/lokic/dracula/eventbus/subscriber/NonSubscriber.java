package com.github.lokic.dracula.eventbus.subscriber;


import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;

public interface NonSubscriber<E extends Event> extends Subscriber<E> {

    @Override
    default void subscribe(E event) {
    }

    @Override
    default void register(Subscription<E> subscription) {

    }

    @Override
    default void unregister(Subscription<E> subscription) {

    }

    @Override
    default void unregister(EventHandler<E> eventHandler) {

    }
}
