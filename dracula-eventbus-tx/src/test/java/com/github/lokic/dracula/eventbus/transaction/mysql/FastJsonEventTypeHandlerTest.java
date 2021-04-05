package com.github.lokic.dracula.eventbus.transaction.mysql;

import com.github.lokic.dracula.event.Event;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;


public class FastJsonEventTypeHandlerTest {

    @Test
    public void deserialize() {
        TestEvent testEvent = new TestEvent();
        testEvent.setContent("xxx");
        testEvent.setId(1L);
        String jsonStr = new FastJsonEventTypeHandler().serialize(testEvent);
        Assert.assertEquals(TestEvent.class, new FastJsonEventTypeHandler().deserialize(jsonStr).getClass());
    }

    @Data
    public static class TestEvent extends Event {
        private Long id;
        private String content;
    }

}