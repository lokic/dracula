package com.github.lokic.dracula.eventbus.transaction;

import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.broker.Broker;
import com.github.lokic.dracula.eventbus.broker.ForwardingBroker;
import com.github.lokic.dracula.eventbus.publisher.Publisher;
import com.github.lokic.dracula.eventbus.subscriber.Subscriber;

public class TransactionalBroker<E extends IntegrationEvent> implements ForwardingBroker<E> {

    private final Subscriber<E> targetSubscriber;

    private final Publisher<E> targetPublisher;


    public TransactionalBroker(Broker<E> targetBroker, TransactionalEventManager management) {
        this.targetSubscriber = targetBroker;
        this.targetPublisher = new TransactionalPublisher<>(targetBroker, management);
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
