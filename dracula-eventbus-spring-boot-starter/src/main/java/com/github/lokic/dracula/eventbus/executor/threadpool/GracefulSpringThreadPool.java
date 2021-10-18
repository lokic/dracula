package com.github.lokic.dracula.eventbus.executor.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 优雅的Spring线程池。
 * <p>
 * 在Spring容器中，建议把改 {@code GracefulSpringThreadPool} 注入到
 * {@link com.github.lokic.dracula.eventbus.executor.AsyncEventExecutor} 中使用。
 * <p>
 * Note:
 * 1. 会等待里面的任务执行完成才关闭
 * 2. 如果线程池满了，会在调用线程池的线程中执行任务 {@link ThreadPoolExecutor.CallerRunsPolicy}
 */
@Slf4j
public class GracefulSpringThreadPool extends ThreadPoolTaskScheduler {

    public GracefulSpringThreadPool(String name, int nThreads) {
        super();
        setThreadFactory(new ThreadFactoryBuilder()
                .setNameFormat(name + "-%d")
                .setDaemon(true)
                .setUncaughtExceptionHandler((t, e) -> log.error("failed to run task on {}.", t, e))
                .build());
        setPoolSize(nThreads);
        setWaitForTasksToCompleteOnShutdown(true);
        setAwaitTerminationSeconds(20);
        setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
