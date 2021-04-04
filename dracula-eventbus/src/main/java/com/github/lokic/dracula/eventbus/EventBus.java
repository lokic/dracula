package com.github.lokic.dracula.eventbus;



import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;
import com.github.lokic.dracula.eventbus.handlers.EventHandlerAttribute;
import com.github.lokic.dracula.eventbus.interceptors.InterceptorAttribute;

import java.util.ArrayList;
import java.util.List;

public interface EventBus {

    default <E extends Event> void register(Class<E> eventClazz, EventHandler<E> handler, EventHandlerAttribute attribute) {
        register(eventClazz, handler, new ArrayList<>(), attribute);
    }

    <E extends Event> void register(Class<E> eventClazz, EventHandler<E> handler, List<InterceptorAttribute<E>> interceptors, EventHandlerAttribute attribute);

    <E extends Event> void unregister(EventHandler<E> handler);

    <E extends Event> void post(E event);

    default <E extends Event> void post(List<E> events) {
        if (events != null) {
            events.forEach(this::post);
        }
    }
}
