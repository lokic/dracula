package com.github.lokic.dracula.eventbus.ddd;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.*;
import com.github.lokic.dracula.event.DomainEvent;
import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.ddd.domian.DomainEventBus;
import com.github.lokic.dracula.eventbus.ddd.integration.IntegrationEventBus;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;
import com.github.lokic.dracula.eventbus.handlers.EventHandlerAttribute;
import com.github.lokic.dracula.eventbus.interceptors.InterceptorAttribute;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutingEventBus implements EventBus {

    private final Map<Class<? extends Event>, EventBus> routingMapping;

    public RoutingEventBus(DomainEventBus domainEventBus, IntegrationEventBus integrationEventBus) {
        Map<Class<? extends Event>, EventBus> routingMapping = new HashMap<>();
        routingMapping.put(DomainEvent.class, domainEventBus);
        routingMapping.put(IntegrationEvent.class, integrationEventBus);
        this.routingMapping = Collections.unmodifiableMap(routingMapping);
    }

    @Override
    public <E extends Event> void register(Class<E> eventClazz, EventHandler<E> handler, List<InterceptorAttribute<E>> interceptors, EventHandlerAttribute attribute) {
        routingMapping.forEach((eClazz, eventBus) -> {
            if (eClazz.isAssignableFrom(eventClazz)) {
                eventBus.register(eventClazz, handler, interceptors, attribute);
            }
        });
    }

    @Override
    public <E extends Event> void unregister(EventHandler<E> handler) {
        routingMapping.forEach((eClazz, eventBus) -> eventBus.unregister(handler));
    }

    @Override
    public <E extends Event> void post(E event) {
        routingMapping.forEach((eClazz, eventBus) -> {
            if (eClazz.isInstance(event)) {
                eventBus.post(event);
            }
        });
    }

}
