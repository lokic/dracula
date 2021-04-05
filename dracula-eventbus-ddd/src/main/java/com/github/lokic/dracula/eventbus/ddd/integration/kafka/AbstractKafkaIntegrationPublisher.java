package com.github.lokic.dracula.eventbus.ddd.integration.kafka;

import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.ddd.integration.Encoder;
import com.github.lokic.dracula.eventbus.ddd.integration.IntegrationPublisher;

public abstract class AbstractKafkaIntegrationPublisher<E extends IntegrationEvent> implements IntegrationPublisher<E> {

    private final Encoder<E> encoder;

    private final String topic;

    private final Partitioner<E> partitioner;


    public AbstractKafkaIntegrationPublisher(String topic, Partitioner<E> partitioner, Encoder<E> encoder) {
        this.encoder = encoder;
        this.topic = topic;
        this.partitioner = partitioner;
    }

    @Override
    public void publish(E event) {
        send(topic, partitioner.key(event), encoder.encode(event));
    }

    public abstract void send(String topic, String key, String msg);

}
