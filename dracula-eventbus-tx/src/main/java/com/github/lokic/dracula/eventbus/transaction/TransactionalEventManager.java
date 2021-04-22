package com.github.lokic.dracula.eventbus.transaction;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.GenericTypes;
import com.github.lokic.dracula.eventbus.publisher.Publisher;
import com.github.lokic.javaplus.Types;
import org.springframework.beans.factory.annotation.Value;

import java.net.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TransactionalEventManager {

    private static final int SCHEDULED_TASK_QUERY_LIMIT = 100;

    private static final long DEFAULT_INIT_BACKOFF = 10L;

    private static final int DEFAULT_BACKOFF_FACTOR = 2;

    private static final int DEFAULT_MAX_RETRY_TIMES = 5;

    private static final LocalDateTime END = LocalDateTime.of(2100, 1, 1, 0, 0, 0);

    private final PublisherManager targetPublisherManager;

    private final TransactionalEventRepository repository;

    @Value("${dracula.event-bus.application.name}")
    private String applicationName;

    private String localIp;

    private String businessKey;


    public TransactionalEventManager(TransactionalEventRepository repository) {
        this.repository = repository;
        this.localIp = localIp();
        this.targetPublisherManager = new PublisherManager();
    }

    public <E extends Event> void send(E event){
        targetPublisherManager.findPublisherForEvent(event)
                .ifPresent(publisher -> publisher.publish(event));
    }

    public void save(List<TransactionalEvent<? extends Event>> transactionalEvents){
        repository.save(transactionalEvents);
    }

    private String getBusinessKey(){
        if (businessKey != null) {
            return businessKey;
        }
        businessKey = applicationName + "@" + localIp;
        return businessKey;
    }

    public void processPendingCompensationEvents() {
        // 时间的右值为当前时间减去退避初始值，这里预防把刚保存的消息也推送了
        LocalDateTime max = LocalDateTime.now().plusSeconds(-DEFAULT_INIT_BACKOFF);
        // 时间的左值为右值减去1小时
        LocalDateTime min = max.plusHours(-1);

        repository.queryEvents(min, max, SCHEDULED_TASK_QUERY_LIMIT)
                .forEach(txEvent -> {
                    try {
                        this.send(txEvent.getEvent());
                        handleSuccess(txEvent);
                    } catch (Exception ex) {
                        handleFail(txEvent, ex);
                    }
                });
    }


    public <E extends Event> void addPublisher(Publisher<E> publisher) {
        targetPublisherManager.addPublisher(publisher);
    }


    private LocalDateTime calculateNextRetryTime(LocalDateTime base,
                                                 long initBackoff,
                                                 long backoffFactor,
                                                 long round) {
        double delta = initBackoff * Math.pow(backoffFactor, round);
        return base.plusSeconds((long) delta);
    }

    public void handleSuccess(TransactionalEvent<? extends Event> txEvent){
        txEvent.setCurrentRetryTimes(calculateRetryTimes(txEvent));
        txEvent.setStatus(TransactionalEvent.Status.SUCCESS);
        txEvent.setNextRetryTime(END);
        txEvent.setEditor(getBusinessKey());
        repository.updateStatus(txEvent);
    }

    public void handleSuccess(List<TransactionalEvent<? extends Event>> txEvents) {
        List<Long> ids = txEvents.stream()
                .map(TransactionalEvent::getId)
                .collect(Collectors.toList());
        repository.updateSuccessByEventIds(getBusinessKey(), END, ids);
    }

    public void handleFail(TransactionalEvent<? extends Event> txEvent, Exception ex){
        Integer currentRetryTimes = calculateRetryTimes(txEvent);
        TransactionalEvent.Status status = calculateStatus(txEvent);
        // 计算下一次的执行时间
        LocalDateTime nextRetryTime = calculateNextRetryTime(
                txEvent.getNextRetryTime(),
                txEvent.getInitBackoff(),
                txEvent.getBackoffFactor(),
                txEvent.getCurrentRetryTimes()
        );

        txEvent.setCurrentRetryTimes(currentRetryTimes);
        txEvent.setNextRetryTime(nextRetryTime);
        txEvent.setStatus(status);
        txEvent.setEditor(getBusinessKey());
        repository.updateStatus(txEvent);
    }

    public TransactionalEvent.Status calculateStatus(TransactionalEvent<? extends Event> txEvent) {
        return calculateRetryTimes(txEvent).compareTo(txEvent.getMaxRetryTimes()) >= 0
                ? TransactionalEvent.Status.FAIL
                : TransactionalEvent.Status.PENDING;
    }

    public Integer calculateRetryTimes(TransactionalEvent<? extends Event> txEvent) {
        return txEvent.getCurrentRetryTimes().compareTo(txEvent.getMaxRetryTimes()) >= 0
                ? txEvent.getMaxRetryTimes()
                : txEvent.getCurrentRetryTimes() + 1;
    }

    private static String localIp() {
        StringBuilder sb = new StringBuilder();
        try {
            // 获取第一个非回环 ipv4 地址
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            label:
            for (NetworkInterface netint : Collections.list(nets)) {
                for (InetAddress addr : Collections.list(netint.getInetAddresses())) {
                    if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                        sb.append(addr.getHostAddress());
                        break label;
                    }
                }
            }
            // 没有ipv4地址
            if(sb.length() == 0) {
                sb.append(InetAddress.getLocalHost().getHostAddress());
            }
        } catch (SocketException e) {
        } catch (UnknownHostException e) {
        }
        return sb.toString();
    }

    public <E extends Event> TransactionalEvent<E> convert(E event) {
        TransactionalEvent<E> txEvent = new TransactionalEvent<>();
        txEvent.setInitBackoff(DEFAULT_INIT_BACKOFF);
        txEvent.setBackoffFactor(DEFAULT_BACKOFF_FACTOR);
        txEvent.setCurrentRetryTimes(0);
        txEvent.setMaxRetryTimes(DEFAULT_MAX_RETRY_TIMES);
        txEvent.setNextRetryTime(calculateNextRetryTime(LocalDateTime.now(),
                DEFAULT_INIT_BACKOFF, DEFAULT_BACKOFF_FACTOR, 0));
        txEvent.setEvent(event);
        txEvent.setStatus(TransactionalEvent.Status.PENDING);
        txEvent.setCreator(getBusinessKey());
        txEvent.setEditor(getBusinessKey());
        return txEvent;
    }


    public static class PublisherManager {

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
}
