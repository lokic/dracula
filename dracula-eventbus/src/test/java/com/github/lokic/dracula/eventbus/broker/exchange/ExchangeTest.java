package com.github.lokic.dracula.eventbus.broker.exchange;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.Queue;
import com.github.lokic.dracula.eventbus.broker.queue.SimpleDelegatingQueue;
import org.junit.Assert;
import org.junit.Test;

public class ExchangeTest {

    @Test
    public void test_event_inherit() {
        Exchange exchange = new Exchange();
        Queue<TestEvent> queue = new SimpleDelegatingQueue<>(TestEvent.class);
        Binding binding = new Binding(new TypeRoutingKey(TestEvent.class), queue);
        exchange.addBinding(binding);

        Assert.assertSame(queue, exchange.route(TestEvent1.class));
        Assert.assertSame(queue, exchange.route(TestEvent2.class));
        Assert.assertNull(exchange.route(NewTestEvent.class));
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