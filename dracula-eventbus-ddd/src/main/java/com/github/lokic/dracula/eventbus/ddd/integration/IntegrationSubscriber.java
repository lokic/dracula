package com.github.lokic.dracula.eventbus.ddd.integration;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.subscriber.Subscriber;

public abstract class IntegrationSubscriber<E extends Event> implements Subscriber<E>, Deserializer<E> {

    private Subscriber<E> subscriber;

    public void connect(Subscriber<E> subscriber){
        if(this.subscriber != null) {
            throw new IllegalStateException("IntegrationSubscriber is connected a Subscriber");
        }
        this.subscriber = subscriber;
    }

    public void receive(String data) {
        subscriber.subscribe(deserialize(data));
    }

}
