package com.github.lokic.dracula.eventbus.queue;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.Queue;
import com.github.lokic.dracula.eventbus.publisher.DelegatingPublisher;
import com.github.lokic.dracula.eventbus.subscriber.DelegatingSubscriber;

public interface DelegatingQueue<E extends Event> extends Queue<E>, DelegatingPublisher<E>, DelegatingSubscriber<E> {

}
