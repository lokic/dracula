package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.eventbus.annotation.PublisherComponent;
import com.github.lokic.dracula.eventbus.event.TestIntegrationEvent;
import com.github.lokic.dracula.eventbus.publisher.TransactionEventPublisher;
import com.github.lokic.dracula.eventbus.transaction.TransactionEventManager;
import org.springframework.context.annotation.DependsOn;

@DependsOn(value = "testIntegrationEventPublisher")
@PublisherComponent
public class TestIntegrationEventTxPublisher extends TransactionEventPublisher<TestIntegrationEvent> {

    public TestIntegrationEventTxPublisher(TransactionEventManager manager) {
        super(TestIntegrationEvent.class, manager);
    }
}
