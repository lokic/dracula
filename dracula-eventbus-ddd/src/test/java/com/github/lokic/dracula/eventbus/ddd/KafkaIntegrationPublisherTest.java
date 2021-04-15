package com.github.lokic.dracula.eventbus.ddd;

import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.broker.Broker;
import com.github.lokic.dracula.eventbus.broker.DefaultBrokerManager;
import com.github.lokic.dracula.eventbus.ddd.integration.Serializer;
import com.github.lokic.dracula.eventbus.ddd.integration.kafka.AbstractKafkaPublisher;
import com.github.lokic.dracula.eventbus.ddd.integration.kafka.Partitioner;
import com.github.lokic.dracula.eventbus.publisher.ForwardingPublisher;
import com.github.lokic.dracula.eventbus.publisher.Publisher;
import com.github.lokic.dracula.eventbus.subscriber.NonSubscriber;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class KafkaIntegrationPublisherTest {

    @Spy
    ProxyKafkaPublisher<OrderCreated> publisher = new ProxyKafkaPublisher<>("xxxxx", Partitioner.dummy(), c -> "aaaa");

    DefaultBrokerManager brokerManager = new DefaultBrokerManager();

    public KafkaIntegrationPublisherTest() {
        MockitoAnnotations.initMocks(this);
        brokerManager.addBroker(OrderCreated.class, new TestBroker(publisher));
    }

    @Test
    public void test_add() {
        brokerManager.getBroker(OrderCreated.class).publish(new OrderCreated());
        Mockito.verify(publisher, Mockito.times(1))
                .send(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }


    @Test
    public void test_notAdd() {
        brokerManager.getBroker(OrderCancelled.class).publish(new OrderCancelled());
        Mockito.verify(publisher, Mockito.never())
                .send(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    public static class TestBroker implements Broker<OrderCreated>, NonSubscriber<OrderCreated>, ForwardingPublisher<OrderCreated> {

        private final Publisher<OrderCreated> publisher;

        public TestBroker(Publisher<OrderCreated> publisher) {
            this.publisher = publisher;
        }

        @Override
        public Publisher<OrderCreated> delegatePublisher() {
            return publisher;
        }
    }

    public static abstract class OrderEvent extends IntegrationEvent {

    }


    public static class OrderCreated extends OrderEvent {

    }

    public static class OrderCancelled extends OrderEvent {

    }

    public static class ProxyKafkaPublisher<E extends IntegrationEvent> extends AbstractKafkaPublisher<E> {

        public ProxyKafkaPublisher(String topic, Partitioner<E> keyMapping, Serializer<E> serializer) {
            super(topic, keyMapping, serializer);
        }

        @Override
        public void send(String topic, String key, String msg) {
        }
    }
}
