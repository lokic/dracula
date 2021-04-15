package com.github.lokic.dracula.eventbus.broker;


import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.GenericTypes;
import com.github.lokic.dracula.eventbus.publisher.Publisher;
import com.github.lokic.dracula.eventbus.subscriber.Subscriber;
import com.github.lokic.javaext.Types;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultBrokerManager implements BrokerManager {

    private final Map<Class<? extends Event>, Broker<? extends Event>> brokers = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public void addWithPublishersAndSubscribers(List<Publisher<? extends Event>> publishers, List<Subscriber<? extends Event>> subscribers) {
        Map<Class<? extends Event>, Publisher<? extends Event>> publisherMap = publishers.stream()
                .collect(Collectors.toMap(p -> GenericTypes.getGeneric(p, Publisher.class), Function.identity()));
        Map<Class<? extends Event>, Subscriber<? extends Event>> subscriberMap = subscribers.stream()
                .collect(Collectors.toMap(s -> GenericTypes.getGeneric(s, Subscriber.class), Function.identity()));
        Sets.SetView<Class<? extends Event>> union = Sets.union(publisherMap.keySet(), publisherMap.keySet());
        Map<Class<? extends Event>, Broker<? extends Event>> brokerMap = union.stream()
                .collect(Collectors.toMap(Function.identity(), eClass -> new DelegateBroker<>(
                        (Publisher<Event>) publisherMap.get(eClass), (Subscriber<Event>) subscriberMap.get(eClass))));
        brokerMap.forEach((type, broker) -> addBroker(Types.cast(type), Types.cast(broker)));
    }

    @SuppressWarnings("unchecked")
    public void addWithBrokers(List<Broker<? extends Event>> brokers) {
        for (Broker<? extends Event> broker : brokers) {
            this.addBroker(GenericTypes.getGeneric(broker, Broker.class), (Broker<Event>) broker);
        }
    }

    public <E extends Event> void addBroker(Class<E> eventClazz, Broker<E> broker) {
        if (brokers.containsKey(eventClazz)) {
            throw new IllegalStateException("event type = " + eventClazz.getName() + " exist broker");
        }
        brokers.put(eventClazz, broker);
    }

    @Override
    public <E extends Event> Broker<E> getBroker(Class<E> eventClazz) {
        return Types.cast(brokers.computeIfAbsent(eventClazz, e -> new InMemoryBroker<E>()));
    }

}
