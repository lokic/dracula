package com.github.lokic.dracula.eventbus.subscriber;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;

import java.io.Serializable;

/**
 * 订阅器，用于订阅者连接Broker
 *
 * @param <E>
 */
public interface Subscriber<E extends Event> extends Serializable {

    void subscribe(E event);

    void register(Subscription<E> subscription);

    void unregister(Subscription<E> subscription);

    void unregister(EventHandler<E> eventHandler);

}
