package com.github.lokic.dracula.eventbus.ddd.integration;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.subscriber.BaseSubscriber;

public abstract class IntegrationSubscriber<E extends Event> extends BaseSubscriber<E> implements Decoder<E> {

    public void receive(String data) {
        subscribe(decode(data));
    }

}
