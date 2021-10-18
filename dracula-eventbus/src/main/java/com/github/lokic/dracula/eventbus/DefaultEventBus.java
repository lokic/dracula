package com.github.lokic.dracula.eventbus;


import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.exchanger.Exchanger;
import com.github.lokic.dracula.eventbus.handler.EventHandler;
import com.github.lokic.dracula.eventbus.handler.EventHandlerAttribute;
import com.github.lokic.dracula.eventbus.interceptor.InterceptorAttribute;
import com.github.lokic.dracula.eventbus.publisher.DelegatingPublisher;
import com.github.lokic.dracula.eventbus.queue.DelegatingQueue;
import com.github.lokic.dracula.eventbus.subscriber.DelegatingSubscriber;
import com.github.lokic.javaplus.Types;

import java.util.List;

public class DefaultEventBus implements EventBus {

    private final Exchanger exchanger;

    public DefaultEventBus() {
        this(new Exchanger());
    }

    public DefaultEventBus(Exchanger exchanger) {
        this.exchanger = exchanger;
    }

    public <E extends Event> void bind(Publisher<E> publisher) {
        Class<E> eventClazz = publisher.getGenericType();
        Queue<E> queue = exchanger.getOrCreateQueue(eventClazz);
        if (queue instanceof DelegatingQueue) {
            bind((DelegatingQueue<E>) queue, publisher);
        }
    }

    public <E extends Event> void bind(Subscriber<E> subscriber) {
        Class<E> eventClazz = subscriber.getGenericType();
        Queue<E> queue = exchanger.getOrCreateQueue(eventClazz);
        if (queue instanceof DelegatingQueue) {
            bind((DelegatingQueue<E>) queue, subscriber);
        }
    }

    @Override
    public <E extends Event> void register(Class<E> eventClazz, EventHandler<E> handler, List<InterceptorAttribute<E>> interceptorAttributes, EventHandlerAttribute eventHandlerAttribute) {
        Subscription<E> subscription = Subscription.of(eventClazz, handler, interceptorAttributes, eventHandlerAttribute);
        Queue<E> queue = exchanger.getOrCreateQueue(eventClazz);
        queue.register(subscription);
    }

    @Override
    public <E extends Event> void unregister(EventHandler<E> handler) {
        Class<E> eventClazz = Types.getGeneric(handler, EventHandler.class);
        Queue<E> queue = exchanger.getQueue(eventClazz);
        if (queue != null) {
            queue.unregister(handler);
        }
    }

    @Override
    public <E extends Event> void send(E event) {
        Queue<E> queue = exchanger.getQueue(Types.getClass(event));
        if (queue != null) {
            queue.publish(event);
        }
    }

    private <E extends Event> void bind(DelegatingQueue<E> queue, Publisher<E> publisher) {
        if (publisher instanceof DelegatingPublisher) {
            ((DelegatingPublisher<E>) publisher).setTargetPublisher(queue.getTargetPublisher());
        }
        queue.setTargetPublisher(publisher);
    }

    private <E extends Event> void bind(DelegatingQueue<E> queue, Subscriber<E> subscriber) {
        if (subscriber instanceof DelegatingSubscriber) {
            ((DelegatingSubscriber<E>) subscriber).setTargetSubscriber(queue.getTargetSubscriber());
        }
        queue.setTargetSubscriber(subscriber);
    }


}
