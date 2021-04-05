package com.github.lokic.dracula.eventbus.subscriber;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;

import java.util.function.Consumer;

public interface Subscription<E extends Event> {

    void process(E event);

    EventHandler<E> getHandler();

    static <E extends Event> Consumer<Subscription<E>> processConsumer(E event) {
        return s -> s.process(event);
    }

}
