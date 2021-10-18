package com.github.lokic.dracula.eventbus.queue;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.Queue;
import com.github.lokic.dracula.eventbus.publisher.ForwardingPublisher;
import com.github.lokic.dracula.eventbus.subscriber.ForwardingSubscriber;

public interface ForwardingQueue<E extends Event> extends Queue<E>, ForwardingPublisher<E>, ForwardingSubscriber<E> {

}
