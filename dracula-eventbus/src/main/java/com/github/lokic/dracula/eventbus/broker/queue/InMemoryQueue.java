package com.github.lokic.dracula.eventbus.broker.queue;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.Publisher;
import com.github.lokic.dracula.eventbus.broker.Subscriber;
import com.github.lokic.dracula.eventbus.broker.Subscription;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InMemoryQueue<E extends Event> implements ForwardingQueue<E> {

    private final Class<E> genericType;
    private final Publisher<E> publisher;
    private final Subscriber<E> subscriber;

    public InMemoryQueue(Class<E> genericType) {
        this.genericType = genericType;
        this.publisher = new InMemoryPublisher<>(genericType, this::subscribe);
        this.subscriber = new InMemorySubscriber<>(genericType);
    }

    @Override
    public Publisher<E> getTargetPublisher() {
        return publisher;
    }

    @Override
    public Subscriber<E> getTargetSubscriber() {
        return subscriber;
    }

    @Override
    public Class<E> getGenericType() {
        return genericType;
    }

    private static class InMemoryPublisher<E extends Event> implements Publisher<E> {
        private static final long serialVersionUID = -3928181039141907942L;
        private final Class<E> genericType;
        private final Consumer<E> publishMethod;

        public InMemoryPublisher(Class<E> genericType, Consumer<E> publishMethod) {
            this.genericType = genericType;
            this.publishMethod = publishMethod;
        }

        @Override
        public void publish(E event) {
            publishMethod.accept(event);
        }

        @Override
        public Class<E> getGenericType() {
            return genericType;
        }
    }

    private static class InMemorySubscriber<E extends Event> implements Subscriber<E> {
        private static final long serialVersionUID = 1941952926429869339L;
        private final Class<E> genericType;
        private final List<Subscription<E>> subscriptions;

        public InMemorySubscriber(Class<E> genericType) {
            this.genericType = genericType;
            this.subscriptions = new ArrayList<>();
        }

        @Override
        public void subscribe(E event) {
            subscriptions.forEach(s -> s.process(event));
        }

        @Override
        public void register(Subscription<E> subscription) {
            subscriptions.add(subscription);
        }

        @Override
        public void unregister(Subscription<E> subscription) {
            subscriptions.removeIf(s -> s == subscription);
        }

        @Override
        public void unregister(EventHandler<E> eventHandler) {
            subscriptions.removeIf(s -> s.getHandler() == eventHandler);
        }

        @Override
        public Class<E> getGenericType() {
            return genericType;
        }
    }
}
