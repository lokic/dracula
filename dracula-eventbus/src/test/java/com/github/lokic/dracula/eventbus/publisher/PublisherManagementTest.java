package com.github.lokic.dracula.eventbus.publisher;

import com.github.lokic.dracula.event.Event;
import org.junit.Assert;
import org.junit.Test;


public class PublisherManagementTest {

    @Test
    public void findEventClassSmart() {
        Assert.assertEquals(TestEvent.class, new PublisherManagement().findEventClassSmart(new Publisher<TestEvent>() {
            @Override
            public void publish(TestEvent event) {

            }
        }) );
    }

    @Test
    public void findEventClassSmart2() {
        Assert.assertEquals(TestEvent.class, new PublisherManagement().findEventClassSmart((Publisher<TestEvent>) event -> {

        }) );
    }


    public static class TestEvent extends Event {

    }
}