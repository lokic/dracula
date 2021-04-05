package com.github.lokic.dracula.eventbus.ddd;

import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.ddd.integration.Encoder;
import com.github.lokic.dracula.eventbus.ddd.integration.kafka.AbstractKafkaIntegrationPublisher;
import com.github.lokic.dracula.eventbus.ddd.integration.kafka.Partitioner;
import com.github.lokic.dracula.eventbus.publisher.PublisherManagement;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class KafkaIntegrationPublisherTest {

    @Spy
    ProxyKafkaIntegrationPublisher<OrderCreated> publisher = new ProxyKafkaIntegrationPublisher<>("xxxxx", Partitioner.dummy(), c -> "aaaa");

    PublisherManagement publisherManagement = new PublisherManagement();

    public KafkaIntegrationPublisherTest() {
        MockitoAnnotations.initMocks(this);
        publisherManagement.addPublisher(OrderCreated.class, publisher);
    }

    @Test
    public void test_add() {
        publisherManagement.processEvent(new OrderCreated());
        Mockito.verify(publisher, Mockito.times(1))
                .send(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }


    @Test
    public void test_notAdd() {
        publisherManagement.processEvent(new OrderCancelled());
        Mockito.verify(publisher, Mockito.never())
                .send(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }


    public static abstract class OrderEvent extends IntegrationEvent {

    }


    public static class OrderCreated extends OrderEvent {

    }

    public static class OrderCancelled extends OrderEvent {

    }

    public static class ProxyKafkaIntegrationPublisher<E extends IntegrationEvent> extends AbstractKafkaIntegrationPublisher<E> {

        public ProxyKafkaIntegrationPublisher(String topic, Partitioner<E> keyMapping, Encoder<E> encoder) {
            super(topic, keyMapping, encoder);
        }

        @Override
        public void send(String topic, String key, String msg) {
        }
    }
}
