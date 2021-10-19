package com.github.lokic.dracula.eventbus.queue;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.Publisher;
import com.github.lokic.dracula.eventbus.Subscriber;
import com.github.lokic.dracula.eventbus.publisher.DelegatingPublisher;
import com.github.lokic.dracula.eventbus.publisher.SimpleDelegatingPublisher;
import com.github.lokic.dracula.eventbus.subscriber.DelegatingSubscriber;
import com.github.lokic.dracula.eventbus.subscriber.SimpleDelegatingSubscriber;

public class SimpleDelegatingQueue<E extends Event> implements DelegatingQueue<E> {

    private final DelegatingPublisher<E> delegatePublisher;

    private final DelegatingSubscriber<E> delegateSubscriber;

    private final Class<E> genericType;

    public SimpleDelegatingQueue(Class<E> genericType) {
        InMemoryQueue<E> queue = new InMemoryQueue<>(genericType);
        this.genericType = genericType;
        this.delegatePublisher = new SimpleDelegatingPublisher<>(genericType, queue);
        this.delegateSubscriber = new SimpleDelegatingSubscriber<>(genericType, queue);
    }

    @Override
    public void setTargetPublisher(Publisher<E> publisher) {
        if (publisher instanceof DelegatingPublisher) {
            ((DelegatingPublisher<E>) publisher).setTargetPublisher(getTargetPublisher());
        }
        delegatePublisher.setTargetPublisher(publisher);
    }

    @Override
    public void setTargetSubscriber(Subscriber<E> subscriber) {
        if (subscriber instanceof DelegatingSubscriber) {
            ((DelegatingSubscriber<E>) subscriber).setTargetSubscriber(getTargetSubscriber());
        }
        delegateSubscriber.setTargetSubscriber(subscriber);
    }


    @Override
    public Publisher<E> getTargetPublisher() {
        return delegatePublisher.getTargetPublisher();
    }

    @Override
    public Subscriber<E> getTargetSubscriber() {
        return delegateSubscriber.getTargetSubscriber();
    }

    @Override
    public Class<E> getGenericType() {
        return genericType;
    }
}
