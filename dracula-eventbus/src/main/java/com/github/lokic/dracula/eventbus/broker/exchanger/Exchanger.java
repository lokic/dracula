package com.github.lokic.dracula.eventbus.broker.exchanger;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.Queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Exchanger {
    private final List<Binding> bindings = new ArrayList<>();

    private final Map<Class<?>, Binding> cache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <E extends Event> Queue<E> route(Class<?> clazz) {
        Binding binding = cache.computeIfAbsent(clazz, this::route0);
        if (binding != null) {
            return (Queue<E>) binding.getQueue();
        }
        return null;
    }

    private Binding route0(Class<?> clazz) {
        return bindings.stream()
                .filter(b -> b.match(clazz))
                .findFirst()
                .orElse(null);
    }

    public void addBinding(Binding binding) {
        bindings.add(binding);
    }

}
