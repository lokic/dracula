package com.github.lokic.dracula.eventbus.transaction;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.publisher.Publisher;

/**
 * 可发布的事件
 * @param <E>
 */
public class PublishableEvent<E extends Event> {

    private final Publisher<E> publisher;

    private final TransactionalEvent<E> transactionalEvent;

    public PublishableEvent(Publisher<E> publisher, TransactionalEvent<E> transactionalEvent) {
        this.publisher = publisher;
        this.transactionalEvent = transactionalEvent;
    }

    public void publish(){
        publisher.publish(transactionalEvent.getEvent());
    }

    public TransactionalEvent<E> getTransactionalEvent() {
        return transactionalEvent;
    }
}
