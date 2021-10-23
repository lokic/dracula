package com.github.lokic.dracula.eventbus.integration.publisher;

import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.Publisher;

import java.util.List;

public abstract class IntegrationPublisher<E extends IntegrationEvent> implements Publisher<E>, Serializer<E> {

    public abstract void send(E event);

    public void send(List<E> events) {
        events.forEach(this::send);
    }

    @Override
    public final void publish(E event) {
        send(event);
    }

    @Override
    public final void publish(List<E> events) {
        send(events);
    }
}
