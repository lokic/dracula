package com.github.lokic.dracula.eventbus.transaction.config;

import com.github.lokic.dracula.eventbus.transaction.TransactionalEventManagement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@Configuration
@ConditionalOnProperty(name = "dracula.event-bus.transaction.publish-mode", havingValue = "scheduling")
@Slf4j
public class SchedulingConfiguration {

    private TransactionalEventManagement transactionalEventManagement;

    public SchedulingConfiguration(TransactionalEventManagement transactionalEventManagement) {
        this.transactionalEventManagement = transactionalEventManagement;
    }

    @Scheduled(fixedDelayString = "${dracula.event-bus.transaction.publish.scheduling.fixed-delay-in-milliseconds}")
    public void transactionalMessageCompensationTask() {
        // todo 考虑是否需要加锁

        long start = System.currentTimeMillis();
        log.info("开始执行事务消息推送补偿定时任务...");
        transactionalEventManagement.processPendingCompensationEvents();
        long end = System.currentTimeMillis();
        long delta = end - start;
        log.info("执行事务消息推送补偿定时任务完毕,耗时:{} ms...", delta);
    }


}
