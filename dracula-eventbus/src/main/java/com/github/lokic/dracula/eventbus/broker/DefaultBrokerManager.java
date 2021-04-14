package com.github.lokic.dracula.eventbus.broker;


import com.github.lokic.dracula.event.Event;
import com.github.lokic.javaext.Types;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBrokerManager implements BrokerManager {

    private final Map<Class<? extends Event>, Broker<? extends Event>> brokers = new ConcurrentHashMap<>();

    public <E extends Event> void addBroker(Class<E> eventClazz, Broker<E> broker) {
        if (brokers.containsKey(eventClazz)) {
            throw new IllegalStateException("event type = " + eventClazz.getName() + " exist broker");
        }
        brokers.put(eventClazz, broker);
    }

    @Override
    public <E extends Event> Broker<E> getBroker(Class<E> eventClazz) {
        return Types.cast(brokers.computeIfAbsent(eventClazz, e -> new InMemoryBroker<E>()));
    }

}
