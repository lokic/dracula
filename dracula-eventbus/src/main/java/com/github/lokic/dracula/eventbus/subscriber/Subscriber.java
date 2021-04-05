package com.github.lokic.dracula.eventbus.subscriber;

import com.github.lokic.dracula.event.Event;

import java.io.Serializable;

public interface Subscriber<E extends Event> extends Serializable {

    void subscribe(E event);
}
