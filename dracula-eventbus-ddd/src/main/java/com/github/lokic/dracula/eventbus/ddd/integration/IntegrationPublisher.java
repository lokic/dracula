package com.github.lokic.dracula.eventbus.ddd.integration;

import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.publisher.Publisher;

public interface IntegrationPublisher<E extends IntegrationEvent> extends Publisher<E> {


}
