package com.github.lokic.dracula.eventbus.subscriber;

import com.github.lokic.dracula.event.Event;

public class LocalSubscriber<E extends Event> extends SubscriptionSubscriber<E> {
    public LocalSubscriber() {
    }
}
