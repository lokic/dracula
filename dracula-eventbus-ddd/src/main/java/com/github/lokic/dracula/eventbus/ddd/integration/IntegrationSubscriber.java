package com.github.lokic.dracula.eventbus.ddd.integration;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.subscriber.ForwardingSubscriber;

public abstract class IntegrationSubscriber<E extends Event> implements ForwardingSubscriber<E>, Deserializer<E> {

    public final void receive(String data) {
        subscribe(deserialize(data));
    }

}
