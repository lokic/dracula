package com.github.lokic.dracula.eventbus.ddd;

import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.broker.Broker;
import com.github.lokic.dracula.eventbus.ddd.integration.Serializer;
import com.github.lokic.dracula.eventbus.ddd.integration.kafka.AbstractKafkaPublisher;
import com.github.lokic.dracula.eventbus.ddd.integration.kafka.Partitioner;
import com.github.lokic.dracula.eventbus.broker.publisher.ForwardingPublisher;
import com.github.lokic.dracula.eventbus.broker.Publisher;
import com.github.lokic.dracula.eventbus.broker.subscriber.NonSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class KafkaIntegrationPublisherTest {

    @Spy
    ProxyKafkaPublisher<OrderCreated> publisher = new ProxyKafkaPublisher<OrderCreated>("xxxxx", Partitioner.dummy(), c -> "aaaa"){
        @Override
        public Class<OrderCreated> getGenericType() {
            return OrderCreated.class;
        }
    };

    Broker broker = new Broker();

    public KafkaIntegrationPublisherTest() {
        MockitoAnnotations.initMocks(this);
        broker.bind(publisher);
    }

    @Test
    public void test_add() {
        broker.publish(new OrderCreated());
        Mockito.verify(publisher, Mockito.times(1))
                .send(Mockito.any(OrderCreated.class));
    }


    @Test
    public void test_notAdd() {
        broker.publish(new OrderCancelled());
        Mockito.verify(publisher, Mockito.never())
                .send(Mockito.any());
    }

    public static class TestBroker implements NonSubscriber<OrderCreated>, ForwardingPublisher<OrderCreated> {

        private final Publisher<OrderCreated> publisher;

        public TestBroker(Publisher<OrderCreated> publisher) {
            this.publisher = publisher;
        }

        @Override
        public Publisher<OrderCreated> getTargetPublisher() {
            return publisher;
        }

        @Override
        public Class<OrderCreated> getGenericType() {
            return OrderCreated.class;
        }
    }

    public static abstract class OrderEvent extends IntegrationEvent {

    }


    public static class OrderCreated extends OrderEvent {

    }

    public static class OrderCancelled extends OrderEvent {

    }

    @Slf4j
    public static abstract class ProxyKafkaPublisher<E extends IntegrationEvent> extends AbstractKafkaPublisher<E> {

        public ProxyKafkaPublisher(String topic, Partitioner<E> keyMapping, Serializer<E> serializer) {
            super(topic, keyMapping, serializer);
        }

        @Override
        public void send(E data) {
            log.info("send kafka topic=" + topic() + ", key=" + partitioner().key(data) +", data=" + serialize(data));
        }
    }
}
