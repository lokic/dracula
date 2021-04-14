package com.github.lokic.dracula.eventbus.ddd.integration.kafka;

import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.ddd.integration.IntegrationPublisher;
import com.github.lokic.dracula.eventbus.ddd.integration.Serializer;

public abstract class AbstractKafkaPublisher<E extends IntegrationEvent> implements IntegrationPublisher<E> {

    private final Serializer<E> serializer;

    private final String topic;

    private final Partitioner<E> partitioner;


    public AbstractKafkaPublisher(String topic, Partitioner<E> partitioner, Serializer<E> serializer) {
        this.serializer = serializer;
        this.topic = topic;
        this.partitioner = partitioner;
    }

    @Override
    public void publish(E event) {
        send(topic, partitioner.key(event), serializer.serialize(event));
    }

    public abstract void send(String topic, String key, String msg);

}
