package com.github.lokic.dracula.eventbus.exchanger;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.Publisher;
import com.github.lokic.dracula.eventbus.Queue;
import com.github.lokic.dracula.eventbus.Subscriber;
import com.github.lokic.dracula.eventbus.queue.DelegatingQueue;
import com.github.lokic.dracula.eventbus.queue.SimpleDelegatingQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Exchanger {
    private final List<Binding> bindings = new ArrayList<>();

    private final Map<Class<?>, Binding> cache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <E extends Event> Queue<E> getQueue(Class<E> eventClazz) {
        Binding binding = cache.computeIfAbsent(eventClazz, this::route);
        if (binding != null) {
            return (Queue<E>) binding.getQueue();
        }
        return null;
    }

    public <E extends Event> Queue<E> createQueue(Class<E> eventClazz) {
        DelegatingQueue<E> delegatingQueue = new SimpleDelegatingQueue<>(eventClazz);
        addBinding(new Binding(new TypeRoutingKey(eventClazz), delegatingQueue));
        return delegatingQueue;
    }

    public <E extends Event> Queue<E> getOrCreateQueue(Class<E> eventClazz) {
        Queue<E> queue = getQueue(eventClazz);
        if (queue != null) {
            return queue;
        }
        return createQueue(eventClazz);
    }

    public <E extends Event> void bind(Publisher<E> publisher) {
        Class<E> eventClazz = publisher.getGenericType();
        Queue<E> queue = getOrCreateQueue(eventClazz);
        if (queue instanceof DelegatingQueue) {
            ((DelegatingQueue<E>) queue).setTargetPublisher(publisher);
        }
    }

    public <E extends Event> void bind(Subscriber<E> subscriber) {
        Class<E> eventClazz = subscriber.getGenericType();
        Queue<E> queue = getOrCreateQueue(eventClazz);
        if (queue instanceof DelegatingQueue) {
            ((DelegatingQueue<E>) queue).setTargetSubscriber(subscriber);
        }
    }

    private Binding route(Class<?> clazz) {
        return bindings.stream()
                .filter(b -> b.match(clazz))
                .findFirst()
                .orElse(null);
    }

    private void addBinding(Binding binding) {
        bindings.add(binding);
    }

}
