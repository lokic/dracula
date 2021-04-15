package com.github.lokic.dracula.eventbus.subscriber;


import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;

import java.util.List;

public interface ForwardingSubscriptionGroup<E extends Event> extends Subscriber<E> {

    List<Subscription<E>> delegateSubscriptionGroup();

    @Override
    default void subscribe(E event){
        delegateSubscriptionGroup().forEach(s -> s.process(event));
    }

    @Override
    default void register(Subscription<E> subscription){
        delegateSubscriptionGroup().add(subscription);
    }

    @Override
    default void unregister(Subscription<E> subscription){
        delegateSubscriptionGroup().removeIf(s -> s == subscription);
    }

    @Override
    default void unregister(EventHandler<E> eventHandler){
        delegateSubscriptionGroup().removeIf(s -> s.getHandler() == eventHandler);
    }
}
