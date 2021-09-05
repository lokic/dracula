package com.github.lokic.dracula.eventbus.ddd.integration.kafka;

import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.ddd.integration.IntegrationPublisher;
import com.github.lokic.dracula.eventbus.ddd.integration.Serializer;

public abstract class AbstractKafkaPublisher<E extends IntegrationEvent> extends IntegrationPublisher<E> {

    private final Serializer<E> serializer;

    private final String topic;

    private final Partitioner<E> partitioner;


    public AbstractKafkaPublisher(String topic, Partitioner<E> partitioner, Serializer<E> serializer) {
        this.serializer = serializer;
        this.topic = topic;
        this.partitioner = partitioner;
    }


    @Override
    public String serialize(E e) {
        return serializer.serialize(e);
    }

    public String topic() {
        return topic;
    }

    public Partitioner<E> partitioner() {
        return partitioner;
    }
}
