package com.github.lokic.dracula.eventbus.transaction;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.Queue;
import com.github.lokic.dracula.eventbus.exchanger.Exchanger;
import com.github.lokic.dracula.eventbus.lock.DistributedLocker;
import com.github.lokic.dracula.eventbus.lock.DistributedLockerFactory;
import com.github.lokic.dracula.eventbus.lock.LockInfo;
import com.github.lokic.dracula.eventbus.publisher.DelegatingPublisher;
import com.github.lokic.javaplus.Types;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.net.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
public class TransactionalEventManager {

    private static final int SCHEDULED_TASK_QUERY_LIMIT = 100;

    private static final long DEFAULT_INIT_BACKOFF = 10L;

    private static final int DEFAULT_BACKOFF_FACTOR = 2;

    private static final int DEFAULT_MAX_RETRY_TIMES = 5;

    private static final LocalDateTime END = LocalDateTime.of(2200, 1, 1, 0, 0, 0);

    private final TransactionalEventRepository repository;

    private final Exchanger exchanger;

    @Value("${dracula.event-bus.application.name}")
    private String applicationName;

    @Value("${dracula.event-bus.transaction.publish.scheduling.fixed-delay-in-milliseconds:1000}")
    private long fixedDelayInMilliseconds;

    @Autowired
    private DistributedLockerFactory distributedLockerFactory;

    private final String localIp;

    private String businessKey;

    private final ScheduledThreadPoolExecutor retryCommit = new ScheduledThreadPoolExecutor(1,
            new ThreadFactoryBuilder().setNameFormat("retry-commit-%d").build());

    public TransactionalEventManager(TransactionalEventRepository repository, Exchanger exchanger) {
        this.repository = repository;
        this.exchanger = exchanger;
        this.localIp = localIp();
    }

    public void init() {
        retryCommit.scheduleAtFixedRate(() -> {
            LockInfo lockInfo = new LockInfo("retryCommit:" + applicationName, UUID.randomUUID().toString(), 1000L);
            try (DistributedLocker locker = distributedLockerFactory.create(lockInfo)) {
                if (locker.tryLock()) {
                    long start = System.currentTimeMillis();
                    log.info("开始执行事务消息推送补偿定时任务...");
                    long end = System.currentTimeMillis();
                    processPendingCompensationEvents();
                    long duration = end - start;
                    log.info("执行事务消息推送补偿定时任务完毕,耗时:{} ms...", duration);
                }
            }
        }, 0, fixedDelayInMilliseconds, TimeUnit.MILLISECONDS);
    }


    @SuppressWarnings("unchecked")
    public <E extends Event> void send(E event) {
        Queue<E> queue = exchanger.getQueue(Types.getClass(event));
        if (queue instanceof DelegatingPublisher) {
            ((DelegatingPublisher<E>) queue).getTargetPublisher().publish(event);
        }
    }

    public void save(List<TransactionalEvent<? extends Event>> transactionalEvents) {
        // 在保存的时候，统一设置下次重试时间，防止在保存之前，时间已经到需要重试的时间了
        LocalDateTime nextRetryTime = calculateNextRetryTime(LocalDateTime.now(), DEFAULT_INIT_BACKOFF, DEFAULT_BACKOFF_FACTOR, 0);
        transactionalEvents.forEach(txEvent -> txEvent.setNextRetryTime(nextRetryTime));
        repository.save(transactionalEvents);
    }

    /**
     * 由 应用名@ip 组成，如果同一机器有多个相同应用会有问题，后面考虑引入端口号
     *
     * @return
     */
    private String getBusinessKey() {
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

    private LocalDateTime calculateNextRetryTime(LocalDateTime base,
                                                 long initBackoff,
                                                 long backoffFactor,
                                                 long round) {
        double delta = initBackoff * Math.pow(backoffFactor, round);
        return base.plusSeconds((long) delta);
    }

    public void handleSuccess(TransactionalEvent<? extends Event> txEvent) {
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

    public void handleFail(TransactionalEvent<? extends Event> txEvent, Exception ex) {
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
        if (txEvent.getStatus() == TransactionalEvent.Status.FAIL) {
            log.error("txEvent retry fail, id = " + txEvent.getId(), ex);
        }
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
            if (sb.length() == 0) {
                sb.append(InetAddress.getLocalHost().getHostAddress());
            }
        } catch (SocketException | UnknownHostException e) {
            log.error("localIp error", e);
        }
        return sb.toString();
    }

    public <E extends Event> TransactionalEvent<E> convert(E event) {
        TransactionalEvent<E> txEvent = new TransactionalEvent<>();
        // todo 基于事件配置
        txEvent.setEventKey("dummy");
        txEvent.setInitBackoff(DEFAULT_INIT_BACKOFF);
        txEvent.setBackoffFactor(DEFAULT_BACKOFF_FACTOR);
        txEvent.setCurrentRetryTimes(0);
        txEvent.setMaxRetryTimes(DEFAULT_MAX_RETRY_TIMES);
        txEvent.setEvent(event);
        txEvent.setStatus(TransactionalEvent.Status.PENDING);
        txEvent.setCreator(getBusinessKey());
        txEvent.setEditor(getBusinessKey());
        return txEvent;
    }

}
