package com.github.lokic.dracula.eventbus.broker;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.GenericTypes;
import com.github.lokic.dracula.eventbus.broker.exchange.Binding;
import com.github.lokic.dracula.eventbus.broker.exchange.Exchange;
import com.github.lokic.dracula.eventbus.broker.exchange.TypeRoutingKey;
import com.github.lokic.dracula.eventbus.broker.publisher.DelegatingPublisher;
import com.github.lokic.dracula.eventbus.broker.queue.DelegatingQueue;
import com.github.lokic.dracula.eventbus.broker.queue.SimpleDelegatingQueue;
import com.github.lokic.dracula.eventbus.broker.subscriber.DelegatingSubscriber;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Broker {

    private final Exchange exchange = new Exchange();

    public <E extends Event> void bind(Publisher<E> publisher) {
        Class<E> eventClazz = publisher.getGenericType();
        Queue<E> queue = exchange.route(eventClazz);
        if (queue == null) {
            DelegatingQueue<E> delegatingQueue = new SimpleDelegatingQueue<>(eventClazz);
            if (publisher instanceof DelegatingPublisher) {
                ((DelegatingPublisher<E>) publisher).setTargetPublisher(delegatingQueue.getTargetPublisher());
            }
            delegatingQueue.setTargetPublisher(publisher);
            exchange.addBinding(new Binding(new TypeRoutingKey(eventClazz), delegatingQueue));
        } else {
            if (queue instanceof DelegatingQueue) {
                if (publisher instanceof DelegatingPublisher) {
                    ((DelegatingPublisher<E>) publisher).setTargetPublisher(((DelegatingQueue<E>) queue).getTargetPublisher());
                }
                ((DelegatingQueue<E>) queue).setTargetPublisher(publisher);
            }
        }
    }

    public <E extends Event> void bind(Subscriber<E> subscriber) {
        Class<E> eventClazz = subscriber.getGenericType();
        Queue<E> queue = exchange.route(eventClazz);
        if (queue == null) {
            DelegatingQueue<E> delegatingQueue = new SimpleDelegatingQueue<>(eventClazz);
            if (subscriber instanceof DelegatingSubscriber) {
                ((DelegatingSubscriber<E>) subscriber).setTargetSubscriber(delegatingQueue.getTargetSubscriber());
            }
            delegatingQueue.setTargetSubscriber(subscriber);
            exchange.addBinding(new Binding(new TypeRoutingKey(eventClazz), delegatingQueue));
        } else {
            if (queue instanceof DelegatingQueue) {
                if (subscriber instanceof DelegatingSubscriber) {
                    ((DelegatingSubscriber<E>) subscriber).setTargetSubscriber(((DelegatingQueue<E>) queue).getTargetSubscriber());
                }
                ((DelegatingQueue<E>) queue).setTargetSubscriber(subscriber);
            }
        }
    }

    public <E extends Event> void publish(E event) {
        Queue<E> queue = exchange.route(event.getClass());
        if (queue != null) {
            queue.publish(event);
        }
    }

    public <E extends Event> void register(Subscription<E> subscription) {
        Class<E> eventClazz = subscription.getGenericType();
        Queue<E> queue = exchange.route(eventClazz);
        if (queue == null) {
            queue = new SimpleDelegatingQueue<>(eventClazz);
            exchange.addBinding(new Binding(new TypeRoutingKey(eventClazz), queue));
        }
        queue.register(subscription);
    }

    public <E extends Event> void unregister(Subscription<E> subscription) {
        Class<E> eventClazz = subscription.getGenericType();
        Queue<E> queue = exchange.route(eventClazz);
        if (queue != null) {
            queue.unregister(subscription);
        }
    }

    public <E extends Event> void unregister(EventHandler<E> eventHandler) {
        Class<E> eventClazz = GenericTypes.getGeneric(eventHandler, EventHandler.class);
        Queue<E> queue = exchange.route(eventClazz);
        if (queue != null) {
            queue.unregister(eventHandler);
        }
    }

}
