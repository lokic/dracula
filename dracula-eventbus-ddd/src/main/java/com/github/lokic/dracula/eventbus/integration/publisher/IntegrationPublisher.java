package com.github.lokic.dracula.eventbus.integration.publisher;

import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.Publisher;

public abstract class IntegrationPublisher<E extends IntegrationEvent> implements Publisher<E>, Serializer<E> {

    public abstract void send(E event);

    @Override
    public final void publish(E event) {
        send(event);
    }
}
