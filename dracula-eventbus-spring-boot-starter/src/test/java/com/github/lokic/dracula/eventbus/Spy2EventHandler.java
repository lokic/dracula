package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.eventbus.annotation.EventHandlerComponent;
import com.github.lokic.dracula.eventbus.event.TestEvent;
import com.github.lokic.dracula.eventbus.executor.AsyncEventExecutor;
import com.github.lokic.dracula.eventbus.handler.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@EventHandlerComponent(executor = AsyncEventExecutor.class)
public class Spy2EventHandler implements EventHandler<TestEvent> {

    @Autowired
    EnableEventBusTest.TestService testService;

    @Override
    public void handle(TestEvent event) {
        log.info("event => {}", event);
    }

}
