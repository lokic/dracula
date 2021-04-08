package com.github.lokic.dracula.eventbus.transaction.mysql;

import com.github.lokic.dracula.event.Event;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;


public class FastJsonEventTypeSerializerTest {

    @Test
    public void deserialize() {
        TestEvent testEvent = new TestEvent();
        testEvent.setContent("xxx");
        testEvent.setId(1L);
        String jsonStr = new FastJsonEventTypeSerializer().serialize(testEvent);
        Assert.assertEquals(TestEvent.class, new FastJsonEventTypeSerializer().deserialize(jsonStr, Event.class).getClass());
    }

    @Data
    public static class TestEvent extends Event {
        private Long id;
        private String content;
    }

}