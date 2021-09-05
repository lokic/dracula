package com.github.lokic.dracula.eventbus.broker.exchange;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.Queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Exchange {
    private final List<Binding> bindings = new ArrayList<>();

    private final Map<Class<?>, Binding> cache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <E extends Event> Queue<E> route(Class<?> clazz) {
        if (cache.get(clazz) != null) {
            return (Queue<E>) cache.get(clazz).getQueue();
        } else {
            Binding binding = route0(clazz);
            if (binding == null) {
                return null;
            }
            cache.put(clazz, binding);
            return (Queue<E>) binding.getQueue();
        }
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
