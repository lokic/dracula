package com.github.lokic.dracula.eventbus.transaction;

import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.publisher.Publisher;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TransactionalPublisher<E extends IntegrationEvent> implements Publisher<E> {

    private final Publisher<E> targetPublisher;

    private final TransactionalEventManagement management;

    public TransactionalPublisher(Publisher<E> targetPublisher,
                                  TransactionalEventManagement management) {
        this.targetPublisher = targetPublisher;
        this.management = management;
        this.management.addPublisher(targetPublisher);
    }

    @Override
    public void publish(E event) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            targetPublisher.publish(event);
        } else {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    TransactionalEventQueue.save(management);
                }

                @Override
                public void afterCommit() {
                    TransactionalEventQueue.publishEvents();
                }

                @Override
                public void afterCompletion(int status) {
                    TransactionalEventQueue.clear();
                }
            });
            TransactionalEventQueue.registerEvent(targetPublisher, event);
        }
    }

}
