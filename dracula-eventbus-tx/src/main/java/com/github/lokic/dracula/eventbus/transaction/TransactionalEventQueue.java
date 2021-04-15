package com.github.lokic.dracula.eventbus.transaction;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.publisher.Publisher;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
public class TransactionalEventQueue {

    private static final ThreadLocal<List<PublishableEvent<? extends Event>>> PUBLISH_EVENTS = ThreadLocal.withInitial(ArrayList::new);

    private static final ThreadLocal<AtomicBoolean> SAVED = ThreadLocal.withInitial(() -> new AtomicBoolean(false));

    private static final ThreadLocal<AtomicBoolean> PUBLISHED = ThreadLocal.withInitial(() -> new AtomicBoolean(false));

    public static <E extends Event> void registerEvent(TransactionalEventManager manager, Publisher<E> publisher, E event) {
        PublishableEvent<E> publishableEvent = new PublishableEvent<E>(publisher, manager.convert(event));
        PUBLISH_EVENTS.get().add(publishableEvent);
    }

    public static void clear() {
        clearEvents();
        resetSavedStatus();
        resetPublishedStatus();
    }

    public static void publishEvents(TransactionalEventManager manager) {
        if (PUBLISHED.get().compareAndSet(false, true)) {
            PUBLISH_EVENTS.get().forEach(PublishableEvent::publish);
            try {
                manager.handleSuccess(getTxEvents());
            } catch (Exception e) {
                // 把消息设置为发功成功时，发生了异常，打印日志之后不做处理，后面会有重试任务重试
                log.error("event handleSuccess after publishEvents happen error", e);
            }
        }
    }

    public static void save(TransactionalEventManager manager) {
        if (SAVED.get().compareAndSet(false, true)) {
            manager.save(getTxEvents());
        }
    }

    private static List<TransactionalEvent<? extends Event>> getTxEvents() {
        return PUBLISH_EVENTS.get().stream()
                .map(PublishableEvent::getTransactionalEvent)
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
