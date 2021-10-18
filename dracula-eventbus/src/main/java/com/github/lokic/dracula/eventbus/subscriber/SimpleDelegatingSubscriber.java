package com.github.lokic.dracula.eventbus.subscriber;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.Subscriber;
import com.github.lokic.dracula.eventbus.queue.InMemoryQueue;

public class SimpleDelegatingSubscriber<E extends Event> implements DelegatingSubscriber<E> {

    private static final long serialVersionUID = -6116848166413233551L;

    private Subscriber<E> targetSubscriber;

    private final Class<E> genericType;

    public SimpleDelegatingSubscriber(Class<E> genericType) {
        InMemoryQueue<E> queue = new InMemoryQueue<>(genericType);
        this.genericType = genericType;
        this.targetSubscriber = queue.getTargetSubscriber();
    }

    public SimpleDelegatingSubscriber(Class<E> genericType, Subscriber<E> subscriber) {
        this.genericType = genericType;
        this.targetSubscriber = subscriber;
    }

    @Override
    public void setTargetSubscriber(Subscriber<E> publisher) {
        this.targetSubscriber = publisher;
    }

    @Override
    public Subscriber<E> getTargetSubscriber() {
        return targetSubscriber;
    }

    @Override
    public Class<E> getGenericType() {
        return genericType;
    }
}
