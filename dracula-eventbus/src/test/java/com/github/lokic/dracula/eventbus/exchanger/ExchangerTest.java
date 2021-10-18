package com.github.lokic.dracula.eventbus.exchanger;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.Queue;
import org.junit.Assert;
import org.junit.Test;

public class ExchangerTest {

    @Test
    public void test_event_inherit() {
        Exchanger exchanger = new Exchanger();
        Queue<TestEvent> queue = exchanger.createQueue(TestEvent.class);

        Assert.assertSame(queue, exchanger.getQueue(TestEvent1.class));
        Assert.assertSame(queue, exchanger.getQueue(TestEvent2.class));
        Assert.assertNull(exchanger.getQueue(NewTestEvent.class));
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