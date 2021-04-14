package com.github.lokic.dracula.eventbus.broker;


import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.publisher.Publisher;
import com.github.lokic.dracula.eventbus.subscriber.Subscriber;

public interface Broker<E extends Event> extends Publisher<E>, Subscriber<E> {
}
