package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;
import com.github.lokic.dracula.eventbus.handlers.EventHandlerAttribute;
import com.github.lokic.dracula.eventbus.interceptors.InterceptorAttribute;
import com.github.lokic.dracula.eventbus.publisher.InMemoryPublisher;
import com.github.lokic.dracula.eventbus.publisher.PublisherManagement;
import com.github.lokic.dracula.eventbus.subscriber.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class InMemoryEventBus implements EventBus {

    private final PublisherManagement publisherManagement;

    private final SubscriberManagement subscriberManagement;

    public InMemoryEventBus(PublisherManagement publisherManagement, SubscriberManagement subscriberManagement) {
        this.publisherManagement = publisherManagement;
        this.subscriberManagement = subscriberManagement;
    }

    @Override
    public <E extends Event> void register(Class<E> eventClazz, EventHandler<E> handler, List<InterceptorAttribute<E>> interceptorAttributes, EventHandlerAttribute eventHandlerAttribute) {
        Subscriber<E> subscriber = subscriberManagement.addSubscriber(eventClazz,
                new InMemorySubscriber<>(new SubscriptionImpl<>(eventClazz, handler, interceptorAttributes, eventHandlerAttribute)));
        publisherManagement.addPublisherIfNotExist(eventClazz, new InMemoryPublisher<>(subscriber));
    }

    @Override
    public <E extends Event> void unregister(EventHandler<E> handler) {
        subscriberManagement.removeSubscriber(handler);
    }

    @Override
    public <E extends Event> void send(E event) {
        publisherManagement.processEvent(event);
    }

}
