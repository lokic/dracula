package com.github.lokic.dracula.eventbus.subscriber;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSubscriber<E extends Event> implements Subscriber<E> {

    public final List<Subscription<E>> subscriptions;

    public BaseSubscriber() {
        this.subscriptions = new ArrayList<>();
    }

    public void addSubscription(Subscription<E> subscription) {
        subscriptions.add(subscription);
    }

    public void removeEventHandler(EventHandler<E> eventHandler) {
        subscriptions.removeIf(s -> s.getHandler() == eventHandler);
    }

    @Override
    public void subscribe(E event) {
        subscriptions.forEach(Subscription.processConsumer(event));
    }
}
