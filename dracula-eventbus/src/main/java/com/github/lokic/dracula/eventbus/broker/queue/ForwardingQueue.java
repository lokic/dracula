package com.github.lokic.dracula.eventbus.broker.queue;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.Queue;
import com.github.lokic.dracula.eventbus.broker.publisher.ForwardingPublisher;
import com.github.lokic.dracula.eventbus.broker.subscriber.ForwardingSubscriber;

public interface ForwardingQueue<E extends Event> extends Queue<E>,  ForwardingPublisher<E>, ForwardingSubscriber<E> {

}
