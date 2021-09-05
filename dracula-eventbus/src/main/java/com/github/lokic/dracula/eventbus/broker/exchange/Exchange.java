package com.github.lokic.dracula.eventbus.broker.exchange;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.Queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Exchange {
    private final List<Binding> bindings = new ArrayList<>();

    private final Map<Class<?>, Optional<Binding>> cache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <E extends Event> Queue<E> route(Class<?> clazz) {
        return (Queue<E>) cache.computeIfAbsent(clazz, this::route0)
                .map(Binding::getQueue)
                .orElse(null);
    }

    private Optional<Binding> route0(Class<?> clazz) {
        return bindings.stream()
                .filter(b -> b.match(clazz))
                .findFirst();
    }

    public void addBinding(Binding binding) {
        bindings.add(binding);
    }

}
