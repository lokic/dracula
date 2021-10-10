package com.github.lokic.dracula.eventbus.broker.exchanger;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.Queue;
import com.github.lokic.dracula.eventbus.broker.queue.SimpleDelegatingQueue;
import org.junit.Assert;
import org.junit.Test;

public class ExchangerTest {

    @Test
    public void test_event_inherit() {
        Exchanger exchanger = new Exchanger();
        Queue<TestEvent> queue = new SimpleDelegatingQueue<>(TestEvent.class);
        Binding binding = new Binding(new TypeRoutingKey(TestEvent.class), queue);
        exchanger.addBinding(binding);

        Assert.assertSame(queue, exchanger.route(TestEvent1.class));
        Assert.assertSame(queue, exchanger.route(TestEvent2.class));
        Assert.assertNull(exchanger.route(NewTestEvent.class));
    }

    public static class TestEvent extends Event {

    }

    public static class TestEvent1 extends TestEvent {

    }

    public static class TestEvent2 extends TestEvent {

    }

    public static class NewTestEvent extends Event {

    }
}