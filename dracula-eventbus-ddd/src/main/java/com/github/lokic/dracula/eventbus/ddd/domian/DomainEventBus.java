package com.github.lokic.dracula.eventbus.ddd.domian;

import com.github.lokic.dracula.eventbus.LocalEventBus;
import com.github.lokic.dracula.eventbus.publisher.PublisherManagement;
import com.github.lokic.dracula.eventbus.subscriber.SubscriberManagement;


public class DomainEventBus extends LocalEventBus {

    public DomainEventBus(PublisherManagement publisherManagement, SubscriberManagement subscriberManagement) {
        super(publisherManagement, subscriberManagement);
    }
}
