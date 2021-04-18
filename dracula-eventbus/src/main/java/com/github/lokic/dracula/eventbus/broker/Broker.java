package com.github.lokic.dracula.eventbus.broker;


import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.publisher.Publisher;
import com.github.lokic.dracula.eventbus.subscriber.Subscriber;

/**
 * 用于连接Publisher和Subscriber，使消息在Publisher和Subscriber之间传递
 *
 * @param <E>
 */
public interface Broker<E extends Event> extends Publisher<E>, Subscriber<E> {
}
