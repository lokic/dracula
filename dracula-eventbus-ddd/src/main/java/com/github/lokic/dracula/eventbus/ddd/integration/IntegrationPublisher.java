package com.github.lokic.dracula.eventbus.ddd.integration;

import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.broker.Publisher;

public abstract class IntegrationPublisher<E extends IntegrationEvent> implements Publisher<E>, Serializer<E> {

    public abstract void send(E event);

    @Override
    public final void publish(E event) {
        send(event);
    }
}
