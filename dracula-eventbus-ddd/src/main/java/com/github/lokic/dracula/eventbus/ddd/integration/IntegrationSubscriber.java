package com.github.lokic.dracula.eventbus.ddd.integration;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.Subscriber;
import com.github.lokic.dracula.eventbus.broker.subscriber.SimpleDelegatingSubscriber;

public abstract class IntegrationSubscriber<E extends Event> extends SimpleDelegatingSubscriber<E> implements Deserializer<E> {

    public IntegrationSubscriber(Class<E> genericType) {
        super(genericType);
    }

    public IntegrationSubscriber(Class<E> genericType, Subscriber<E> subscriber) {
        super(genericType, subscriber);
    }

    public void receive(String data) {
        subscribe(deserialize(data));
    }

}
