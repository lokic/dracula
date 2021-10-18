package com.github.lokic.dracula.eventbus.subscriber;


import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.Subscriber;
import com.github.lokic.dracula.eventbus.Subscription;
import com.github.lokic.dracula.eventbus.handler.EventHandler;

public interface ForwardingSubscriber<E extends Event> extends Subscriber<E> {

    Subscriber<E> getTargetSubscriber();

    @Override
    default void subscribe(E event) {
        getTargetSubscriber().subscribe(event);
    }

    @Override
    default void register(Subscription<E> subscription) {
        getTargetSubscriber().register(subscription);
    }

    @Override
    default void unregister(Subscription<E> subscription) {
        getTargetSubscriber().unregister(subscription);
    }

    @Override
    default void unregister(EventHandler<E> eventHandler) {
        getTargetSubscriber().unregister(eventHandler);
    }
}
