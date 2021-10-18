package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.event.Event;

public interface Queue<E extends Event> extends Publisher<E>, Subscriber<E> {

}
