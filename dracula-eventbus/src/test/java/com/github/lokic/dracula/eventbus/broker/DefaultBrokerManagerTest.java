package com.github.lokic.dracula.eventbus.broker;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.publisher.NonPublisher;
import com.github.lokic.dracula.eventbus.publisher.Publisher;
import com.github.lokic.dracula.eventbus.subscriber.NonSubscriber;
import com.github.lokic.dracula.eventbus.subscriber.Subscriber;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.List;
import java.util.Map;


public class DefaultBrokerManagerTest {


    @Test
    public void test_addWithPublishersAndSubscribers() {
        Publisher<TestEvent1> publisher1 = new NonPublisher<TestEvent1>() {
        };


        Publisher<TestEvent2> publisher2 = new NonPublisher<TestEvent2>() {
        };

        Subscriber<TestEvent2> subscriber2 = new NonSubscriber<TestEvent2>() {

        };

        Subscriber<TestEvent3> subscriber3 = new NonSubscriber<TestEvent3>() {
        };


        List<Publisher<? extends Event>> publishers = Lists.newArrayList(publisher1, publisher2);
        List<Subscriber<? extends Event>> subscribers = Lists.newArrayList(subscriber2, subscriber3);

        DefaultBrokerManager brokerManager = new DefaultBrokerManager();
        brokerManager.addWithPublishersAndSubscribers(publishers, subscribers);

        Assert.assertEquals(3, ((Map<?, ?>) Whitebox.getInternalState(brokerManager, "brokers")).size());
    }


    public static class TestEvent1 extends Event {

    }

    public static class TestEvent2 extends Event {

    }

    public static class TestEvent3 extends Event {

    }
}