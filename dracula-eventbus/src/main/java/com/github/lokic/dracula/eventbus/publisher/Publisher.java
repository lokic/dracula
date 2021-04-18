package com.github.lokic.dracula.eventbus.publisher;

import com.github.lokic.dracula.event.Event;

import java.io.Serializable;

/**
 * 发送器，用于发送者连接Broker
 *
 * @param <E>
 */
public interface Publisher<E extends Event> extends Serializable {

    void publish(E event);
}
