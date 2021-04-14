package com.github.lokic.dracula.eventbus.transaction;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.Broker;
import com.github.lokic.dracula.eventbus.subscriber.ForwardingSubscriber;
import com.github.lokic.dracula.eventbus.subscriber.Subscriber;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TransactionalBroker<E extends Event> implements Broker<E>, ForwardingSubscriber<E> {

    private final Broker<E> targetBroker;
    private final TransactionalEventManager management;

    public TransactionalBroker(Broker<E> targetBroker, TransactionalEventManager management) {
        this.targetBroker = targetBroker;
        this.management = management;
        this.management.addPublisher(targetBroker);
    }

    @Override
    public void publish(E event) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            targetBroker.publish(event);
        } else {
            TransactionalEventQueue.registerEvent(management, targetBroker, event);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    TransactionalEventQueue.save(management);
                }

                @Override
                public void afterCommit() {
                    TransactionalEventQueue.publishEvents(management);
                }

                @Override
                public void afterCompletion(int status) {
                    TransactionalEventQueue.clear();
                }
            });
        }
    }

    @Override
    public Subscriber<E> delegateSubscriber() {
        return targetBroker;
    }
}
