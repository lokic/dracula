package com.github.lokic.dracula.eventbus.publisher;


import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.Publisher;

/**
 * 什么都不做的Publisher，一般用于如果只有订阅，没有发送的场景
 *
 * @param <E>
 */
public interface NonPublisher<E extends Event> extends Publisher<E> {

    @Override
    default void publish(E event) {

    }
}
