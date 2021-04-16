package com.github.lokic.dracula.eventbus.ddd.integration;

import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.publisher.Publisher;

public abstract class IntegrationPublisher<E extends IntegrationEvent> implements Publisher<E>, Serializer<E> {

    public abstract void send(String data);

    @Override
    public final void publish(E event) {
        send(serialize(event));
    }
}
