package com.github.lokic.dracula.eventbus.broker.queue;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.Publisher;
import com.github.lokic.dracula.eventbus.broker.Subscriber;
import com.github.lokic.dracula.eventbus.broker.publisher.DelegatingPublisher;
import com.github.lokic.dracula.eventbus.broker.publisher.SimpleDelegatingPublisher;
import com.github.lokic.dracula.eventbus.broker.subscriber.DelegatingSubscriber;
import com.github.lokic.dracula.eventbus.broker.subscriber.SimpleDelegatingSubscriber;

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
        delegatePublisher.setTargetPublisher(publisher);
    }

    @Override
    public void setTargetSubscriber(Subscriber<E> subscriber) {
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
