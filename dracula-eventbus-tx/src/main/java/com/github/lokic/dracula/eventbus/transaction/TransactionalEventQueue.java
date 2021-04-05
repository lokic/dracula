package com.github.lokic.dracula.eventbus.transaction;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.publisher.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class TransactionalEventQueue {

    private static final ThreadLocal<List<PublishableEvent<? extends Event>>> PUBLISH_EVENTS = ThreadLocal.withInitial(ArrayList::new);

    private static final ThreadLocal<AtomicBoolean> SAVED = ThreadLocal.withInitial(() -> new AtomicBoolean(false));

    private static final ThreadLocal<AtomicBoolean> PUBLISHED = ThreadLocal.withInitial(() -> new AtomicBoolean(false));

    public static <E extends Event> void registerEvent(Publisher<E> publisher, E event) {
        PublishableEvent<E> publishableEvent = new PublishableEvent<E>(publisher, event);
        PUBLISH_EVENTS.get().add(publishableEvent);
    }

    public static void clear() {
        clearEvents();
        resetSavedStatus();
        resetPublishedStatus();
    }

    public static void publishEvents() {
        if (PUBLISHED.get().compareAndSet(false, true)) {
           PUBLISH_EVENTS.get().forEach(PublishableEvent::publish);
        }
    }

    public static void save(TransactionalEventManagement manager) {
        if (SAVED.get().compareAndSet(false, true)) {
            manager.save(getEvents());
        }
    }

    private static List<? extends Event> getEvents() {
        return PUBLISH_EVENTS.get().stream()
                .map(PublishableEvent::getEvent)
                .collect(Collectors.toList());
    }

    private static void clearEvents() {
        PUBLISH_EVENTS.remove();
    }

    private static void resetSavedStatus() {
        SAVED.get().getAndSet(false);
    }

    private static void resetPublishedStatus() {
        PUBLISHED.get().getAndSet(false);
    }


}
