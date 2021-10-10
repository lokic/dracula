package com.github.lokic.dracula.eventbus.broker;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.GenericTypes;
import com.github.lokic.dracula.eventbus.broker.exchanger.Binding;
import com.github.lokic.dracula.eventbus.broker.exchanger.Exchanger;
import com.github.lokic.dracula.eventbus.broker.exchanger.TypeRoutingKey;
import com.github.lokic.dracula.eventbus.broker.publisher.DelegatingPublisher;
import com.github.lokic.dracula.eventbus.broker.queue.DelegatingQueue;
import com.github.lokic.dracula.eventbus.broker.queue.SimpleDelegatingQueue;
import com.github.lokic.dracula.eventbus.broker.subscriber.DelegatingSubscriber;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Broker {

    private final Exchanger exchanger = new Exchanger();

    public <E extends Event> void bind(Publisher<E> publisher) {
        Class<E> eventClazz = publisher.getGenericType();
        Queue<E> queue = routeOrAddBinding(eventClazz);
        if (queue instanceof DelegatingQueue) {
            bind((DelegatingQueue<E>) queue, publisher);
        }
    }


    public <E extends Event> void bind(Subscriber<E> subscriber) {
        Class<E> eventClazz = subscriber.getGenericType();
        Queue<E> queue = routeOrAddBinding(eventClazz);
        if (queue instanceof DelegatingQueue) {
            bind((DelegatingQueue<E>) queue, subscriber);
        }
    }

    public <E extends Event> void register(Subscription<E> subscription) {
        Class<E> eventClazz = subscription.getGenericType();
        Queue<E> queue = routeOrAddBinding(eventClazz);
        queue.register(subscription);
    }

    public <E extends Event> void unregister(Subscription<E> subscription) {
        Class<E> eventClazz = subscription.getGenericType();
        Queue<E> queue = exchanger.route(eventClazz);
        if (queue != null) {
            queue.unregister(subscription);
        }
    }

    public <E extends Event> void unregister(EventHandler<E> eventHandler) {
        Class<E> eventClazz = GenericTypes.getGeneric(eventHandler, EventHandler.class);
        Queue<E> queue = exchanger.route(eventClazz);
        if (queue != null) {
            queue.unregister(eventHandler);
        }
    }

    public <E extends Event> void publish(E event) {
        Queue<E> queue = exchanger.route(event.getClass());
        if (queue != null) {
            queue.publish(event);
        }
    }

    private <E extends Event> Queue<E> routeOrAddBinding(Class<E> eventClazz) {
        Queue<E> queue = exchanger.route(eventClazz);
        if (queue != null) {
            return queue;
        }
        DelegatingQueue<E> delegatingQueue = new SimpleDelegatingQueue<>(eventClazz);
        exchanger.addBinding(new Binding(new TypeRoutingKey(eventClazz), delegatingQueue));
        return delegatingQueue;
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
