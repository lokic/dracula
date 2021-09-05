package com.github.lokic.dracula.eventbus.ddd.integration;

import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.broker.Broker;
import com.github.lokic.dracula.eventbus.broker.Subscription;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;
import lombok.ToString;
import org.junit.Test;
import org.mockito.Mockito;

public class IntegrationSubscriberTest {

    @Test
    public void test_receive() {
        Broker broker = new Broker();


        EventHandler<TestEvent> eventHandler1 = Mockito.spy(new EventHandler<TestEvent>() {
            @Override
            public void handle(TestEvent event) {
            }
        });

        EventHandler<TestEvent> eventHandler2 = Mockito.spy(new EventHandler<TestEvent>() {
            @Override
            public void handle(TestEvent event) {
            }
        });


        KafkaIntegrationSubscriber subscriber = new KafkaIntegrationSubscriber();
        broker.bind(subscriber);
        broker.register(Subscription.simple(eventHandler1));
        broker.register(Subscription.simple(eventHandler2));
        subscriber.receive("xxx");


        Mockito.verify(eventHandler1, Mockito.times(1))
                .handle(Mockito.any(TestEvent.class));

        Mockito.verify(eventHandler2, Mockito.times(1))
                .handle(Mockito.any(TestEvent.class));

    }

    public static class KafkaIntegrationSubscriber extends IntegrationSubscriber<TestEvent> {
        private static final long serialVersionUID = -8575356336671154446L;

        public KafkaIntegrationSubscriber() {
            super(TestEvent.class);
        }

        @Override
        public TestEvent deserialize(String s) {
            return new TestEvent(s);
        }
    }

    @ToString
    public static class TestEvent extends IntegrationEvent {

        private final String s;

        public TestEvent(String s) {
            this.s = s;
        }
    }

}