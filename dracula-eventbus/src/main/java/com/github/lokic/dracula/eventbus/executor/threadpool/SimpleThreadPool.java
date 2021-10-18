package com.github.lokic.dracula.eventbus.executor.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 简单异步执行器。在Spring容器中存在优雅关闭的问题，所以该Executor适合一些消息丢失也不要紧的场景
 */
@Slf4j
public class SimpleThreadPool extends ThreadPoolExecutor {

    public SimpleThreadPool(String name, int nThreads, int buffer) {
        super(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(buffer),
                new ThreadFactoryBuilder()
                        .setNameFormat(name + "-%d")
                        .setDaemon(true)
                        .setUncaughtExceptionHandler((t, e) -> log.error("failed to run task on {}.", t, e))
                        .build(),
                new CallerRunsPolicy());
    }
}
