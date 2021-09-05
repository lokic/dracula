package com.github.lokic.dracula.eventbus.broker.subscriber;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.Subscriber;

public interface DelegatingSubscriber<E extends Event> extends ForwardingSubscriber<E> {

    void setTargetSubscriber(Subscriber<E> subscriber);

}
