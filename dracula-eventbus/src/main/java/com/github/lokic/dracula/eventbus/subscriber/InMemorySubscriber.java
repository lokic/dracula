package com.github.lokic.dracula.eventbus.subscriber;

import com.github.lokic.dracula.event.Event;

public class InMemorySubscriber<E extends Event> extends SubscriptionSubscriber<E> {

    public InMemorySubscriber() {
    }

    public InMemorySubscriber(Subscription<E> subscription) {
        super(subscription);
    }
}
