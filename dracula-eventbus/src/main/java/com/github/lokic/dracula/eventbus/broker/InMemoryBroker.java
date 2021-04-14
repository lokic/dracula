package com.github.lokic.dracula.eventbus.broker;


import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.subscriber.ForwardingSubscriptionGroup;
import com.github.lokic.dracula.eventbus.subscriber.Subscription;

import java.util.ArrayList;
import java.util.List;

public class InMemoryBroker<E extends Event> implements Broker<E>, ForwardingSubscriptionGroup<E> {

    private final List<Subscription<E>> subscriptions;

    public InMemoryBroker() {
        this.subscriptions = new ArrayList<>();
    }

    @Override
    public void publish(E event) {
        subscribe(event);
    }

    @Override
    public List<Subscription<E>> delegateSubscriptionGroup() {
        return subscriptions;
    }
}
