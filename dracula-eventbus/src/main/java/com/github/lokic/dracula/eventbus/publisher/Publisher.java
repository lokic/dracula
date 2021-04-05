package com.github.lokic.dracula.eventbus.publisher;

import com.github.lokic.dracula.event.Event;

import java.io.Serializable;

public interface Publisher<E extends Event> extends Serializable {

    void publish(E event);
}
