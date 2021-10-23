package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.event.Event;

import java.io.Serializable;
import java.util.List;

/**
 * 发送器，用于发送者连接EventBus
 *
 * @param <E>
 */
public interface Publisher<E extends Event> extends Serializable {

    default void publish(List<E> events) {
        events.forEach(this::publish);
    }

    void publish(E event);

    Class<E> getGenericType();
}
