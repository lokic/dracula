package com.github.lokic.dracula.eventbus.subscriber;


import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;

public interface ForwardingSubscriber<E extends Event> extends Subscriber<E> {

    Subscriber<E> delegateSubscriber();

    @Override
    default void subscribe(E event) {
        delegateSubscriber().subscribe(event);
    }

    @Override
    default void register(Subscription<E> subscription) {
        delegateSubscriber().register(subscription);
    }

    @Override
    default void unregister(Subscription<E> subscription) {
        delegateSubscriber().unregister(subscription);
    }

    @Override
    default void unregister(EventHandler<E> eventHandler) {
        delegateSubscriber().unregister(eventHandler);
    }
}
