package com.github.lokic.dracula.eventbus.broker;


import com.github.lokic.dracula.event.Event;

public interface BrokerManager {
    <E extends Event> Broker<E> getBroker(Class<E> eventClazz);
}
