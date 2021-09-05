package com.github.lokic.dracula.eventbus;


import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.Broker;
import com.github.lokic.dracula.eventbus.broker.Subscription;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;
import com.github.lokic.dracula.eventbus.handlers.EventHandlerAttribute;
import com.github.lokic.dracula.eventbus.interceptors.InterceptorAttribute;

import java.util.List;

public class DefaultEventBus implements EventBus {

    private final Broker broker;

    public DefaultEventBus(Broker broker) {
        this.broker = broker;
    }

    @Override
    public <E extends Event> void register(Class<E> eventClazz, EventHandler<E> handler, List<InterceptorAttribute<E>> interceptorAttributes, EventHandlerAttribute eventHandlerAttribute) {
        Subscription<E> subscription = Subscription.of(eventClazz, handler, interceptorAttributes, eventHandlerAttribute);
        broker.register(subscription);
    }

    @Override
    public <E extends Event> void unregister(EventHandler<E> handler) {
        broker.unregister(handler);
    }

    @Override
    public <E extends Event> void send(E event) {
        broker.publish(event);
    }


}
