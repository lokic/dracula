package com.github.lokic.dracula.eventbus.ddd.domian;

import com.github.lokic.dracula.eventbus.InMemoryEventBus;
import com.github.lokic.dracula.eventbus.publisher.PublisherManagement;
import com.github.lokic.dracula.eventbus.subscriber.SubscriberManagement;


public class DomainEventBus extends InMemoryEventBus {

    public DomainEventBus(PublisherManagement publisherManagement, SubscriberManagement subscriberManagement) {
        super(publisherManagement, subscriberManagement);
    }
}
