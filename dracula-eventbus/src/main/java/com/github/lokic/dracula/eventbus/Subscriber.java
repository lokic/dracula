package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handler.EventHandler;

import java.io.Serializable;

/**
 * 订阅器，用于订阅者连接EventBus
 *
 * @param <E>
 */
public interface Subscriber<E extends Event> extends Serializable {

    void subscribe(E event);

    void register(Subscription<E> subscription);

    void unregister(Subscription<E> subscription);

    void unregister(EventHandler<E> eventHandler);

    Class<E> getGenericType();
}
