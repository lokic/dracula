package com.github.lokic.dracula.eventbus.integration.subscriber;

import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.DefaultEventBus;
import com.github.lokic.dracula.eventbus.exchanger.Exchanger;
import com.github.lokic.dracula.eventbus.handler.EventHandler;
import com.github.lokic.dracula.eventbus.handler.EventHandlerAttribute;
import lombok.ToString;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

public class IntegrationSubscriberTest {

    @Test
    public void test_receive() {
        Exchanger exchanger = new Exchanger();
        DefaultEventBus eventBus = new DefaultEventBus(exchanger);


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
        exchanger.bind(subscriber);
        eventBus.register(TestEvent.class, eventHandler1, new ArrayList<>(), EventHandlerAttribute.sync());
        eventBus.register(TestEvent.class, eventHandler2, new ArrayList<>(), EventHandlerAttribute.sync());
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