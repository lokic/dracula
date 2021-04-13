package com.github.lokic.dracula.eventbus.ddd.integration;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.*;import com.github.lokic.dracula.eventbus.handlers.EventHandler;
import com.github.lokic.dracula.eventbus.publisher.PublisherManagement;
import com.github.lokic.dracula.eventbus.subscriber.*;
import com.github.lokic.dracula.eventbus.handlers.EventHandlerAttribute;
import com.github.lokic.dracula.eventbus.interceptors.InterceptorAttribute;

import java.util.List;
import java.util.Objects;

public class IntegrationEventBus implements EventBus {

    private final SubscriberManagement subscriberManagement;

    private final PublisherManagement publisherManagement;

    public IntegrationEventBus(PublisherManagement publisherManagement, SubscriberManagement subscriberManagement) {
        this.subscriberManagement = subscriberManagement;
        this.publisherManagement = publisherManagement;
    }

    @Override
    public <E extends Event> void register(Class<E> eventClazz, EventHandler<E> handler, List<InterceptorAttribute<E>> interceptors, EventHandlerAttribute attribute) {
        subscriberManagement.addSubscription(eventClazz, new SubscriptionImpl<>(eventClazz, handler, interceptors, attribute));
    }

    public <E extends IntegrationEvent> void connect(Class<E> eventClazz, IntegrationSubscriber<E> subscriber) {
        Subscriber<E> s = subscriberManagement.getSubscriber(eventClazz);
        subscriber.connect(Objects.requireNonNull(s));
    }

    @Override
    public <E extends Event> void unregister(EventHandler<E> handler) {
        subscriberManagement.removeEventHandler(handler);
    }

    @Override
    public <E extends Event> void send(E event) {
        publisherManagement.processEvent(event);
    }

}
