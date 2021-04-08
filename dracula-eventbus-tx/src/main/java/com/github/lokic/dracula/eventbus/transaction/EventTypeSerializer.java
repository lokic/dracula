package com.github.lokic.dracula.eventbus.transaction;

import com.github.lokic.dracula.event.Event;

public interface EventTypeSerializer {
    <E extends Event> E deserialize(String s, Class<E> eventClazz);

    String serialize(Event event);
}
