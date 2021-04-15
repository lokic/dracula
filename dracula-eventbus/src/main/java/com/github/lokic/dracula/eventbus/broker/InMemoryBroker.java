package com.github.lokic.dracula.eventbus.broker;


import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.publisher.Publisher;
import com.github.lokic.dracula.eventbus.subscriber.ForwardingSubscriptionGroup;
import com.github.lokic.dracula.eventbus.subscriber.Subscriber;
import com.github.lokic.dracula.eventbus.subscriber.Subscription;

import java.util.ArrayList;
import java.util.List;

public class InMemoryBroker<E extends Event> implements ForwardingBroker<E> {

    private final List<Subscription<E>> subscriptions;

    private final Publisher<E> publisher;
    private final Subscriber<E> subscriber;

    public InMemoryBroker() {
        this.subscriptions = new ArrayList<>();
        this.publisher = this::subscribe;
        this.subscriber = (ForwardingSubscriptionGroup<E>) () -> subscriptions;
    }

    @Override
    public Publisher<E> delegatePublisher() {
        return publisher;
    }

    @Override
    public Subscriber<E> delegateSubscriber() {
        return subscriber;
    }

}

