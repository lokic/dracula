package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.eventbus.annotation.PublisherComponent;
import com.github.lokic.dracula.eventbus.event.TestIntegrationEvent;
import com.github.lokic.dracula.eventbus.publisher.TransactionalEventPublisher;
import com.github.lokic.dracula.eventbus.transaction.TransactionalEventManager;
import org.springframework.context.annotation.DependsOn;

@DependsOn(value = "testIntegrationEventPublisher")
@PublisherComponent
public class TestIntegrationEventTxPublisher extends TransactionalEventPublisher<TestIntegrationEvent> {

    public TestIntegrationEventTxPublisher(TransactionalEventManager manager) {
        super(TestIntegrationEvent.class, manager);
    }
}
