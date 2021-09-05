package com.github.lokic.dracula.eventbus.handlers;

import com.github.lokic.dracula.event.Event;

import java.io.Serializable;

public interface EventHandler<E extends Event> extends Serializable {

    void handle(E event);

}
