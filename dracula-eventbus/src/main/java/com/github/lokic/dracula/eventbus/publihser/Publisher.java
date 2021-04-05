package com.github.lokic.dracula.eventbus.publihser;

import com.github.lokic.dracula.event.Event;

public interface Publisher<E extends Event> {

    void publish(E event);
}
