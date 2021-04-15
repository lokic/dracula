package com.github.lokic.dracula.eventbus.broker;


import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.publisher.Publisher;
import com.github.lokic.dracula.eventbus.subscriber.ForwardingSubscriptionGroup;
import com.github.lokic.dracula.eventbus.subscriber.Subscriber;
import com.github.lokic.dracula.eventbus.subscriber.Subscription;

import java.util.ArrayList;
import java.util.List;

public class DelegateBroker<E extends Event> implements ForwardingBroker<E> {

    private final List<Subscription<E>> subscriptions;

    private final Publisher<E> targetPublisher;

    private final Subscriber<E> targetSubscriber;

    public DelegateBroker(Publisher<E> targetPublisher, Subscriber<E> targetSubscriber) {
        this.subscriptions = new ArrayList<>();
        this.targetPublisher = targetPublisher != null ? targetPublisher : this::subscribe;
        this.targetSubscriber = targetSubscriber != null ? targetSubscriber : (ForwardingSubscriptionGroup<E>) () -> subscriptions;
    }

    @Override
    public Publisher<E> delegatePublisher() {
        return targetPublisher;
    }

    @Override
    public Subscriber<E> delegateSubscriber() {
        return targetSubscriber;
    }
}
