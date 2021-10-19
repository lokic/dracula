package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.eventbus.annotation.PublisherComponent;
import com.github.lokic.dracula.eventbus.event.TestIntegrationEvent;
import org.springframework.beans.factory.annotation.Autowired;

@PublisherComponent
public class TestIntegrationEventPublisher implements Publisher<TestIntegrationEvent> {

    @Autowired
    EnableEventBusTest.TestService testService;

    @Override
    public void publish(TestIntegrationEvent event) {
        testService.method();
    }

    @Override
    public Class<TestIntegrationEvent> getGenericType() {
        return TestIntegrationEvent.class;
    }

}
