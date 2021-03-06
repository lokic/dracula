package com.github.lokic.dracula.eventbus.transaction;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.transaction.serializer.FastJsonEventTypeSerializer;
import com.github.lokic.dracula.eventbus.transaction.serializer.JacksonEventTypeSerializer;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;


public class EventTypeSerializerTest {

    @Test
    public void fastjson() {
        EventTypeSerializer eventTypeSerializer = new FastJsonEventTypeSerializer();
        TestEvent testEvent = new TestEvent();
        testEvent.setContent("xxx");
        testEvent.setId(1L);
        String jsonStr = eventTypeSerializer.serialize(testEvent);
        Assert.assertEquals(TestEvent.class, eventTypeSerializer.deserialize(jsonStr, Event.class).getClass());
    }

    @Test
    public void jackson() {
        EventTypeSerializer eventTypeSerializer = new JacksonEventTypeSerializer();
        TestEvent testEvent = new TestEvent();
        testEvent.setContent("xxx");
        testEvent.setId(1L);
        String jsonStr = eventTypeSerializer.serialize(testEvent);
        Assert.assertEquals(TestEvent.class, eventTypeSerializer.deserialize(jsonStr, Event.class).getClass());
    }


    @Data
    public static class TestEvent extends Event {
        private Long id;
        private String content;
    }

}