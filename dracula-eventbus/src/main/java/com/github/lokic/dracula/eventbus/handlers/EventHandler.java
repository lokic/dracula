package com.github.lokic.dracula.eventbus.handlers;

import com.github.lokic.dracula.event.Event;

public interface EventHandler<E extends Event> {

    void handle(E event);



}
