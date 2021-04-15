package com.github.lokic.dracula.eventbus.transaction;

import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.publisher.Publisher;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TransactionalPublisher<E extends IntegrationEvent> implements Publisher<E> {

    private final Publisher<E> targetPublisher;

    private final TransactionalEventManager manager;

    public TransactionalPublisher(Publisher<E> targetPublisher,
                                  TransactionalEventManager manager) {
        this.targetPublisher = targetPublisher;
        this.manager = manager;
        this.manager.addPublisher(targetPublisher);
    }

    @Override
    public void publish(E event) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            targetPublisher.publish(event);
        } else {
            TransactionalEventQueue.registerEvent(manager, targetPublisher, event);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    TransactionalEventQueue.save(manager);
                }

                @Override
                public void afterCommit() {
                    TransactionalEventQueue.publishEvents(manager);
                }

                @Override
                public void afterCompletion(int status) {
                    TransactionalEventQueue.clear();
                }
            });
        }
    }

}
