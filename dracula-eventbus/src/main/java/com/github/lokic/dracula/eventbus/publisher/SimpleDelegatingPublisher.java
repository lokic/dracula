package com.github.lokic.dracula.eventbus.publisher;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.Publisher;
import com.github.lokic.dracula.eventbus.queue.InMemoryQueue;

public class SimpleDelegatingPublisher<E extends Event> implements DelegatingPublisher<E> {
    private Publisher<E> targetPublisher;
    private final Class<E> genericType;

    public SimpleDelegatingPublisher(Class<E> genericType) {
        InMemoryQueue<E> queue = new InMemoryQueue<>(genericType);
        this.genericType = genericType;
        this.targetPublisher = queue.getTargetPublisher();
    }

    public SimpleDelegatingPublisher(Class<E> genericType, Publisher<E> publisher) {
        this.genericType = genericType;
        this.targetPublisher = publisher;
    }

    @Override
    public void setTargetPublisher(Publisher<E> publisher) {
        this.targetPublisher = publisher;
    }

    @Override
    public Publisher<E> getTargetPublisher() {
        return targetPublisher;
    }

    @Override
    public Class<E> getGenericType() {
        return genericType;
    }
}
