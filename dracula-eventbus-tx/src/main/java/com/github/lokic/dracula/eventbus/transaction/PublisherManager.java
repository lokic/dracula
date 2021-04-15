package com.github.lokic.dracula.eventbus.transaction;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.GenericTypes;
import com.github.lokic.dracula.eventbus.publisher.Publisher;
import com.github.lokic.javaext.Types;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PublisherManager {

    public Map<Class<? extends Event>, Publisher<? extends Event>> publishers;

    public PublisherManager() {
        publishers = new ConcurrentHashMap<>();
    }

    /**
     * 如果publisher不存在，则添加；否则抛异常
     *
     * @param eventClazz
     * @param publisher
     * @param <E>
     */
    public <E extends Event> void addPublisher(Class<E> eventClazz, Publisher<E> publisher) {
        if (hasPublishersForEvent(eventClazz)) {
            throw new IllegalStateException(String.format("eventClass = %s exist publisher", eventClazz.getName()));
        }
        publishers.put(eventClazz, publisher);
    }

    public <E extends Event> void addPublisher(Publisher<E> publisher) {
        Class<E> eventClazz = GenericTypes.getGeneric(publisher, Publisher.class);
        addPublisher(eventClazz, publisher);
    }

    /**
     * 如果publisher不存在，则添加；否则忽略
     *
     * @param eventClazz
     * @param publisher
     * @param <E>
     */
    public <E extends Event> void addPublisherIfAbsent(Class<E> eventClazz, Publisher<E> publisher) {
        if (!hasPublishersForEvent(eventClazz)) {
            publishers.put(eventClazz, publisher);
        }
    }

    public <E extends Event> Optional<Publisher<E>> findPublisherForEvent(E event) {
        return Optional.ofNullable(findPublisher(Types.getClass(event)));
    }

    private <E extends Event> boolean hasPublishersForEvent(Class<E> eventClazz) {
        return publishers.containsKey(eventClazz);
    }

    private <E extends Event> Publisher<E> findPublisher(Class<E> eventClazz) {
        return Types.cast(publishers.get(eventClazz));
    }

    public <E extends Event> void processEvent(E event) {
        findPublisherForEvent(event)
                .ifPresent(p -> p.publish(event));
    }
}
