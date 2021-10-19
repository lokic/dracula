package com.github.lokic.dracula.eventbus.integration.kafka;

import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.DefaultEventBus;
import com.github.lokic.dracula.eventbus.exchanger.Exchanger;
import com.github.lokic.dracula.eventbus.integration.publisher.Serializer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class KafkaIntegrationPublisherTest {

    @Spy
    ProxyKafkaPublisher<OrderCreated> publisher = new ProxyKafkaPublisher<OrderCreated>("xxxxx", Partitioner.dummy(), c -> "aaaa") {
        @Override
        public Class<OrderCreated> getGenericType() {
            return OrderCreated.class;
        }
    };

    Exchanger exchanger = new Exchanger();
    DefaultEventBus eventBus = new DefaultEventBus(exchanger);

    public KafkaIntegrationPublisherTest() {
        MockitoAnnotations.initMocks(this);
        exchanger.bind(publisher);
    }

    @Test
    public void test_add() {
        eventBus.send(new OrderCreated());
        Mockito.verify(publisher, Mockito.times(1))
                .send(Mockito.any(OrderCreated.class));
    }


    @Test
    public void test_notAdd() {
        eventBus.send(new OrderCancelled());
        Mockito.verify(publisher, Mockito.never())
                .send(Mockito.any());
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
            log.info("send kafka topic=" + topic() + ", key=" + partitioner().key(data) + ", data=" + serialize(data));
        }
    }
}
