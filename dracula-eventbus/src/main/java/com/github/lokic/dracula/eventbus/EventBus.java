package com.github.lokic.dracula.eventbus;


import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handler.EventHandler;
import com.github.lokic.dracula.eventbus.handler.EventHandlerAttribute;
import com.github.lokic.dracula.eventbus.interceptor.InterceptorAttribute;

import java.util.ArrayList;
import java.util.List;

public interface EventBus {

    default <E extends Event> void register(Class<E> eventClazz, EventHandler<E> handler, EventHandlerAttribute attribute) {
        register(eventClazz, handler, new ArrayList<>(), attribute);
    }

    <E extends Event> void register(Class<E> eventClazz, EventHandler<E> handler, List<InterceptorAttribute<E>> interceptors, EventHandlerAttribute attribute);

    <E extends Event> void unregister(EventHandler<E> handler);

    <E extends Event> void send(E event);

    default <E extends Event> void send(List<E> events) {
        if (events != null) {
            events.forEach(this::send);
        }
    }
}
