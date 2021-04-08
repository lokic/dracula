package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.publisher.Publisher;
import org.junit.Assert;
import org.junit.Test;


public class GenericTypesTest {

    @Test
    public void findEventClassSmart() {
        Assert.assertEquals(TestEvent.class, GenericTypes.getGeneric(new Publisher<TestEvent>() {
            @Override
            public void publish(TestEvent event) {

            }
        }, Publisher.class) );
    }

    @Test
    public void findEventClassSmart2() {
        Assert.assertEquals(TestEvent.class, GenericTypes.getGeneric((Publisher<TestEvent>) event -> { }, Publisher.class ));
    }

    @Test
    public void findEventClassSmart3() {
        Assert.assertEquals(TestEvent.class, GenericTypes.getGeneric(new MyPublish(), Publisher.class ));
    }


    public static class TestEvent extends Event {

    }

    public static class MyPublish implements Publisher<TestEvent> {
        @Override
        public void publish(TestEvent event) {

        }
    }
}