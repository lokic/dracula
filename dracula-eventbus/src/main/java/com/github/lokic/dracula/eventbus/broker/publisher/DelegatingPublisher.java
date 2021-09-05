package com.github.lokic.dracula.eventbus.broker.publisher;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.Publisher;

public interface DelegatingPublisher<E extends Event> extends ForwardingPublisher<E> {
    void setTargetPublisher(Publisher<E> publisher);
}
