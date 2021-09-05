package com.github.lokic.dracula.eventbus.broker.queue;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.Queue;
import com.github.lokic.dracula.eventbus.broker.publisher.DelegatingPublisher;
import com.github.lokic.dracula.eventbus.broker.subscriber.DelegatingSubscriber;

public interface DelegatingQueue<E extends Event> extends Queue<E>, DelegatingPublisher<E>, DelegatingSubscriber<E> {

}
