package com.github.lokic.dracula.eventbus.broker;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.exchange.Exchange;
import com.github.lokic.dracula.eventbus.broker.publisher.DelegatingPublisher;
import com.github.lokic.dracula.eventbus.broker.publisher.NonPublisher;
import com.github.lokic.dracula.eventbus.broker.queue.SimpleDelegatingQueue;
import com.github.lokic.dracula.eventbus.broker.subscriber.NonSubscriber;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.util.List;

public class BrokerTest {

    @Test
    public void test_bind() {
        Publisher<TestEvent1> publisher1 = new NonPublisher<TestEvent1>() {

            @Override
            public Class<TestEvent1> getGenericType() {
                return TestEvent1.class;
            }
        };

        Publisher<TestEvent2> publisher2 = new NonPublisher<TestEvent2>() {
            @Override
            public Class<TestEvent2> getGenericType() {
                return TestEvent2.class;
            }
        };

        Subscriber<TestEvent2> subscriber2 = new NonSubscriber<TestEvent2>() {
            @Override
            public Class<TestEvent2> getGenericType() {
                return TestEvent2.class;
            }

        };

        Subscriber<TestEvent3> subscriber3 = new NonSubscriber<TestEvent3>() {
            @Override
            public Class<TestEvent3> getGenericType() {
                return TestEvent3.class;
            }
        };

        List<Publisher<? extends Event>> publishers = Lists.newArrayList(publisher1, publisher2);
        List<Subscriber<? extends Event>> subscribers = Lists.newArrayList(subscriber2, subscriber3);

        Broker broker = new Broker();
        publishers.forEach(broker::bind);
        subscribers.forEach(broker::bind);
        Exchange exchange = Whitebox.getInternalState(broker, "exchange");
        List<?> bindings = Whitebox.getInternalState(exchange, "bindings");
        Assert.assertEquals(3, bindings.size());
    }

    @Test
    public void test_wrap() {
        DelegatingPublisher<TestEvent1> wrapPublisher = Mockito.spy(new TestDelegatePublisher());
        Publisher<TestEvent1> publisher = Mockito.spy(new TestPublisher());

        Broker broker = new Broker();
        broker.bind(publisher);
        broker.bind(wrapPublisher);

        broker.publish(new TestEvent1());

        Mockito.verify(wrapPublisher, Mockito.times(1))
                .publish(Mockito.any(TestEvent1.class));

        Mockito.verify(publisher, Mockito.times(1))
                .publish(Mockito.any(TestEvent1.class));

    }

    public static class TestPublisher implements Publisher<TestEvent1> {
        @Override
        public void publish(TestEvent1 event) {

        }

        @Override
        public Class<TestEvent1> getGenericType() {
            return TestEvent1.class;
        }
    }

    public static class TestDelegatePublisher extends SimpleDelegatingQueue<TestEvent1> {
        public TestDelegatePublisher() {
            super(TestEvent1.class);
        }
    }


    public static class TestEvent1 extends Event {

    }

    public static class TestEvent2 extends Event {

    }

    public static class TestEvent3 extends Event {

    }
}