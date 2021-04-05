package com.github.lokic.dracula.eventbus.transaction;

import com.github.lokic.dracula.event.Event;

public interface EventTypeHandler {
    Event deserialize(String s);

    String serialize(Event event);
}
