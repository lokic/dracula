package com.github.lokic.dracula.eventbus.subscriber;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;
import com.github.lokic.javaext.Types;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SubscriberManagement {

    public Map<Class<? extends Event>, SubscriptionSubscriber<? extends Event>> subscribers;


    public SubscriberManagement() {
        this.subscribers = new ConcurrentHashMap<>();
    }

    public <E extends Event> SubscriptionSubscriber<E> addSubscription(Class<E> eClazz, Subscription<E> subscription) {
        SubscriptionSubscriber<E> subscriber = Types.cast(subscribers.computeIfAbsent(eClazz, e -> new InMemorySubscriber<>()));
        subscriber.addSubscription(subscription);
        return subscriber;
    }

    public <E extends Event> void removeSubscriber(EventHandler<E> eventHandler) {
        subscribers.values().forEach(subscriber -> subscriber.removeSubscription(Types.cast(eventHandler)));
    }

    public <E extends Event> void replaceSubscriber(Class<E> eClazz, SubscriptionSubscriber<E> subscriber) {
        SubscriptionSubscriber<E> oldSub = getSubscriber(eClazz);
        oldSub.getSubscriptions().forEach(subscriber::addSubscription);
        subscribers.put(eClazz, subscriber);
    }

    public <E extends Event> SubscriptionSubscriber<E> getSubscriber(Class<E> eClazz) {
        return Types.cast(subscribers.get(eClazz));
    }

}
