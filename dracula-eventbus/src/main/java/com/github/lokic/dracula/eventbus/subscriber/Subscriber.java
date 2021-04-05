package com.github.lokic.dracula.eventbus.subscriber;

import com.github.lokic.dracula.event.Event;

public interface Subscriber<E extends Event> {

    void subscribe(E event);
}
