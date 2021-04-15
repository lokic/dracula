package com.github.lokic.dracula.eventbus.broker;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.publisher.ForwardingPublisher;
import com.github.lokic.dracula.eventbus.subscriber.ForwardingSubscriber;

public interface ForwardingBroker<E extends Event> extends Broker<E>, ForwardingPublisher<E>, ForwardingSubscriber<E> {
}
