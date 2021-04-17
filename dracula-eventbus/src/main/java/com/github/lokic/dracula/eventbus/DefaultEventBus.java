package com.github.lokic.dracula.eventbus;


import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.BrokerManager;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;
import com.github.lokic.dracula.eventbus.handlers.EventHandlerAttribute;
import com.github.lokic.dracula.eventbus.interceptors.InterceptorAttribute;
import com.github.lokic.dracula.eventbus.subscriber.Subscription;
import com.github.lokic.dracula.eventbus.subscriber.SubscriptionImpl;
import com.github.lokic.javaplus.Types;

import java.util.List;

public class DefaultEventBus implements EventBus {

    private final BrokerManager brokerManager;

    public DefaultEventBus(BrokerManager brokerManager) {
        this.brokerManager = brokerManager;
    }

    @Override
    public <E extends Event> void register(Class<E> eventClazz, EventHandler<E> handler, List<InterceptorAttribute<E>> interceptorAttributes, EventHandlerAttribute eventHandlerAttribute) {
        Subscription<E> subscription = new SubscriptionImpl<>(eventClazz, handler, interceptorAttributes, eventHandlerAttribute);
        brokerManager.getBroker(eventClazz).register(subscription);
    }

    @Override
    public <E extends Event> void unregister(EventHandler<E> handler) {
        Class<E> eventClazz = GenericTypes.getGeneric(handler, EventHandler.class);
        brokerManager.getBroker(eventClazz).unregister(handler);
    }

    @Override
    public <E extends Event> void send(E event) {
        brokerManager.getBroker(Types.getClass(event)).publish(event);
    }


}
