package com.github.lokic.dracula.eventbus;


import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.exchanger.Exchanger;
import com.github.lokic.dracula.eventbus.handler.EventHandler;
import com.github.lokic.dracula.eventbus.handler.EventHandlerAttribute;
import com.github.lokic.dracula.eventbus.interceptor.InterceptorAttribute;
import com.github.lokic.javaplus.Types;

import java.util.List;

public class DefaultEventBus implements EventBus {

    private final Exchanger exchanger;

    public DefaultEventBus(Exchanger exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public <E extends Event> void register(Class<E> eventClazz, EventHandler<E> handler, List<InterceptorAttribute<? extends E>> interceptorAttributes, EventHandlerAttribute eventHandlerAttribute) {
        Subscription<E> subscription = Subscription.of(eventClazz, handler, interceptorAttributes, eventHandlerAttribute);
        Queue<E> queue = exchanger.getOrCreateQueue(eventClazz);
        queue.register(subscription);
    }

    @Override
    public <E extends Event> void unregister(EventHandler<E> handler) {
        Class<E> eventClazz = Types.getGeneric(handler, EventHandler.class);
        Queue<E> queue = exchanger.getQueue(eventClazz);
        if (queue != null) {
            queue.unregister(handler);
        }
    }

    @Override
    public <E extends Event> void send(E event) {
        Queue<E> queue = exchanger.getQueue(Types.getClass(event));
        if (queue != null) {
            queue.publish(event);
        }
    }

}
