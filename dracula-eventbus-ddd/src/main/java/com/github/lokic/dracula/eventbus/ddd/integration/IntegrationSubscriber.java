package com.github.lokic.dracula.eventbus.ddd.integration;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.subscriber.SubscriptionSubscriber;

public class IntegrationSubscriber<E extends Event> extends SubscriptionSubscriber<E> {

    public void receive(Decoder<E> decoder, String data) {
        subscribe(decoder.decode(data));
    }

}
