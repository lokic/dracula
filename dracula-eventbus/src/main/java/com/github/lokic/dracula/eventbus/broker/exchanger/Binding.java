package com.github.lokic.dracula.eventbus.broker.exchanger;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.Queue;

public class Binding {

    private final RoutingKey routingKey;

    private final Queue<? extends Event> queue;

    public Binding(RoutingKey routingKey, Queue<? extends Event> queue) {
        this.routingKey = routingKey;
        this.queue = queue;
    }

    public boolean match(Class<?> clazz) {
        return routingKey.match(clazz);
    }

    public Queue<? extends Event> getQueue() {
        return queue;
    }
}
