package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.eventbus.annotation.EventHandlerComponent;
import com.github.lokic.dracula.eventbus.event.TestEvent;
import com.github.lokic.dracula.eventbus.handler.EventHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EventHandlerComponent
public class Spy1EventHandler implements EventHandler<TestEvent> {

    final EnableEventBusTest.TestService testService;

    public Spy1EventHandler(EnableEventBusTest.TestService testService) {
        this.testService = testService;
    }

    @Override
    public void handle(TestEvent event) {
        log.info("event => {}", event);
    }
}
