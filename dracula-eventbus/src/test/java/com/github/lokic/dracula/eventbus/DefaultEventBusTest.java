package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.exchanger.Exchanger;
import com.github.lokic.dracula.eventbus.publisher.DelegatingPublisher;
import com.github.lokic.dracula.eventbus.publisher.NonPublisher;
import com.github.lokic.dracula.eventbus.queue.SimpleDelegatingQueue;
import com.github.lokic.dracula.eventbus.subscriber.NonSubscriber;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.util.List;

public class DefaultEventBusTest {

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

        DefaultEventBus eventBus = new DefaultEventBus();
        publishers.forEach(eventBus::bind);
        subscribers.forEach(eventBus::bind);
        Exchanger exchanger = Whitebox.getInternalState(eventBus, "exchanger");
        List<?> bindings = Whitebox.getInternalState(exchanger, "bindings");
        Assert.assertEquals(3, bindings.size());
    }

    @Test
    public void test_wrap() {
        DelegatingPublisher<TestEvent1> wrapPublisher = Mockito.spy(new TestDelegatePublisher());
        Publisher<TestEvent1> publisher = Mockito.spy(new TestPublisher());

        DefaultEventBus eventBus = new DefaultEventBus();
        eventBus.bind(publisher);
        eventBus.bind(wrapPublisher);

        eventBus.send(new TestEvent1());

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