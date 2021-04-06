package com.github.lokic.dracula.eventbus.ddd.integration;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.*;import com.github.lokic.dracula.eventbus.handlers.EventHandler;
import com.github.lokic.dracula.eventbus.publisher.PublisherManagement;
import com.github.lokic.dracula.eventbus.subscriber.*;
import com.github.lokic.dracula.eventbus.handlers.EventHandlerAttribute;
import com.github.lokic.dracula.eventbus.interceptors.InterceptorAttribute;

import java.util.List;

public class IntegrationEventBus implements EventBus {

    private final SubscriberManagement subscriberManagement;

    private final PublisherManagement publisherManagement;

    public IntegrationEventBus(PublisherManagement publisherManagement, SubscriberManagement subscriberManagement) {
        this.subscriberManagement = subscriberManagement;
        this.publisherManagement = publisherManagement;
    }

    @Override
    public <E extends Event> void register(Class<E> eventClazz, EventHandler<E> handler, List<InterceptorAttribute<E>> interceptors, EventHandlerAttribute attribute) {
        InMemorySubscriber<E> subscriber = new InMemorySubscriber<>(new SubscriptionImpl<>(eventClazz, handler, interceptors, attribute));
        subscriberManagement.addSubscriber(eventClazz, subscriber);
    }

    public <E extends IntegrationEvent> void connect(Class<E> eventClazz, IntegrationSubscriber<E> subscriber) {
        Subscriber<E> s = subscriberManagement.getSubscriber(eventClazz);
        if (s instanceof IntegrationSubscriber) {
            throw new IllegalArgumentException(eventClazz.getName() + " already connect IntegrationSubscriber");
        } else {
            subscriberManagement.replaceSubscriber(eventClazz, subscriber);
        }
    }

    @Override
    public <E extends Event> void unregister(EventHandler<E> handler) {
        subscriberManagement.removeSubscriber(handler);
    }

    @Override
    public <E extends Event> void post(E event) {
        publisherManagement.processEvent(event);
    }

}
