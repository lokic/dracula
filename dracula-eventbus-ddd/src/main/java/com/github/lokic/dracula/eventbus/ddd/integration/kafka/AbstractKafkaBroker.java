package com.github.lokic.dracula.eventbus.ddd.integration.kafka;


import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.broker.Broker;
import com.github.lokic.dracula.eventbus.publisher.ForwardingPublisher;
import com.github.lokic.dracula.eventbus.publisher.Publisher;

public abstract class AbstractKafkaBroker<E extends IntegrationEvent> implements Broker<E>, ForwardingPublisher<E> {

    private final AbstractKafkaPublisher<E> publisher;

    public AbstractKafkaBroker(AbstractKafkaPublisher<E> publisher) {
        this.publisher = publisher;
    }

    @Override
    public Publisher<E> delegatePublisher() {
        return publisher;
    }


}
