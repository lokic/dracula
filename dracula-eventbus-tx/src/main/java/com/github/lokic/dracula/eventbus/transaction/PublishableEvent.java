package com.github.lokic.dracula.eventbus.transaction;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.publisher.Publisher;

/**
 * 可发布的事件
 * @param <E>
 */
public class PublishableEvent<E extends Event> {

    Publisher<E> publisher;

    E event;

    public PublishableEvent(Publisher<E> publisher, E event) {
        this.publisher = publisher;
        this.event = event;
    }

    public void publish(){
        publisher.publish(event);
    }

    public E getEvent() {
        return event;
    }

}
