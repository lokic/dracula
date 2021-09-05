package com.github.lokic.dracula.eventbus.broker;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.GenericTypes;
import com.github.lokic.dracula.eventbus.broker.subscriber.DefaultSubscription;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;
import com.github.lokic.dracula.eventbus.handlers.EventHandlerAttribute;
import com.github.lokic.dracula.eventbus.interceptors.InterceptorAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * EventHandler中相关订阅信息的封装
 *
 * @param <E>
 */
public interface Subscription<E extends Event> {

    void process(E event);

    EventHandler<E> getHandler();

    Class<E> getGenericType();

    static <E extends Event> Subscription<E> of(Class<E> eventClazz, EventHandler<E> handler, List<InterceptorAttribute<E>> interceptorAttributes, EventHandlerAttribute eventHandlerAttribute) {
        return new DefaultSubscription<>(eventClazz, handler, interceptorAttributes, eventHandlerAttribute);
    }


    static <E extends Event> Subscription<E> simple(EventHandler<E> handler) {
        Class<E> eventClazz = GenericTypes.getGeneric(handler, EventHandler.class);
        return new DefaultSubscription<>(eventClazz, handler, new ArrayList<>(), EventHandlerAttribute.sync());
    }

}
