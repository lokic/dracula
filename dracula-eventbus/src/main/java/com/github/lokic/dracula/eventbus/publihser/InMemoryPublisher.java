package com.github.lokic.dracula.eventbus.publihser;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.subscriber.Subscriber;

public class InMemoryPublisher<E extends Event> implements Publisher<E> {

    public final Subscriber<E> subscriber;

    public InMemoryPublisher(Subscriber<E> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void publish(E event) {
        subscriber.subscribe(event);
    }
}
