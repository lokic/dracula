package com.github.lokic.dracula.eventbus.publisher;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.transaction.TransactionalEvent;
import com.github.lokic.dracula.eventbus.transaction.TransactionalEventManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class TransactionalEventPublisher<E extends Event> extends SimpleDelegatingPublisher<E> {

    /**
     * 等待发送的消息
     */
    private static final ThreadLocal<List<? extends Event>> WAITING_PUBLISH_EVENTS = ThreadLocal.withInitial(ArrayList::new);


    /**
     * 事务同步是否已注册，确保一个事务只注册一次
     */
    private static final ThreadLocal<AtomicBoolean> TX_REGISTERED = ThreadLocal.withInitial(() -> new AtomicBoolean(false));

    private final TransactionalEventManager manager;

    public TransactionalEventPublisher(Class<E> genericType, TransactionalEventManager manager) {
        super(genericType);
        this.manager = manager;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void publish(E event) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            getTargetPublisher().publish(event);
        } else {
            ((List<E>) WAITING_PUBLISH_EVENTS.get()).add(event);
            if (TX_REGISTERED.get().compareAndSet(false, true)) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    private final List<TransactionalEvent<? extends Event>> txEvents = new ArrayList<>();

                    @Override
                    public void beforeCommit(boolean readOnly) {
                        for (Event evt : WAITING_PUBLISH_EVENTS.get()) {
                            txEvents.add(manager.initTransactionEvent(evt));
                        }
                        manager.save(txEvents);
                    }

                    @Override
                    public void afterCommit() {
                        // 为了确保发送的时效，会在事务提交之后，主动发送一次消息
                        try {
                            getTargetPublisher().publish((List<E>) WAITING_PUBLISH_EVENTS.get());
                            manager.handleSuccess(txEvents);
                        } catch (Exception e) {
                            // 发生了异常，打印日志之后不做处理，后面会有重试任务重试
                            log.error("afterCommit happen error", e);
                        }
                    }

                    @Override
                    public void afterCompletion(int status) {
                        clear();
                    }
                });
            }
        }
    }

    private void clear() {
        clearEvents();
        resetStatus();
    }

    private void clearEvents() {
        WAITING_PUBLISH_EVENTS.remove();
    }

    private void resetStatus() {
        TX_REGISTERED.get().getAndSet(false);
    }

}
