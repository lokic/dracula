package com.github.lokic.dracula.eventbus.ddd.integration;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.publisher.ForwardingPublisher;
import com.github.lokic.dracula.eventbus.subscriber.Subscriber;

public abstract class IntegrationSubscriber<E extends Event> implements Subscriber<E>, ForwardingPublisher<E>, Deserializer<E> {

    public void receive(String data) {
        subscribe(deserialize(data));
    }

}
