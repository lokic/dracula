package com.github.lokic.dracula.eventbus.subscriber;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;
import com.github.lokic.javaext.Types;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SubscriberManagement {

    public Map<Class<? extends Event>, BaseSubscriber<? extends Event>> subscribers;


    public SubscriberManagement() {
        this.subscribers = new ConcurrentHashMap<>();
    }

    public <E extends Event> BaseSubscriber<E> addSubscription(Class<E> eClazz, Subscription<E> subscription) {
        BaseSubscriber<E> subscriber = Types.cast(subscribers.computeIfAbsent(eClazz, e -> new LocalSubscriber<>()));
        subscriber.addSubscription(subscription);
        return subscriber;
    }

    public <E extends Event> void removeEventHandler(EventHandler<E> eventHandler) {
        subscribers.values().forEach(subscriber -> subscriber.removeEventHandler(Types.cast(eventHandler)));
    }

    public <E extends Event> void replaceSubscriber(Class<E> eClazz, BaseSubscriber<E> subscriber) {
        BaseSubscriber<E> oldSub = getSubscriber(eClazz);
        oldSub.getSubscriptions().forEach(subscription -> {
            subscriber.addSubscription(subscription);
            oldSub.removeSubscription(subscription);
        });
        subscribers.put(eClazz, subscriber);
    }

    public <E extends Event> BaseSubscriber<E> getSubscriber(Class<E> eClazz) {
        return Types.cast(subscribers.computeIfAbsent(eClazz, e -> new LocalSubscriber<>()));
    }

}
