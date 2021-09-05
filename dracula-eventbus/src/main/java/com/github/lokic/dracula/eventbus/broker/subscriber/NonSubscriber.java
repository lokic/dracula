package com.github.lokic.dracula.eventbus.broker.subscriber;


import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.Subscriber;
import com.github.lokic.dracula.eventbus.broker.Subscription;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;

/**
 * 什么都不做的Subscriber，一般用于如果只有发送，没有订阅的场景
 *
 * @param <E>
 */
public interface NonSubscriber<E extends Event> extends Subscriber<E> {

    @Override
    default void subscribe(E event) {
    }

    @Override
    default void register(Subscription<E> subscription) {

    }

    @Override
    default void unregister(Subscription<E> subscription) {

    }

    @Override
    default void unregister(EventHandler<E> eventHandler) {

    }
}
