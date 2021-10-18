package com.github.lokic.dracula.eventbus.executor;

import com.github.lokic.dracula.event.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

/**
 * 异步事件执行器适配，事件分发和事件处理在不同线程上下文内完成。
 * <p>
 * Note：
 * 在不同线程中执行操作，事务一致性难以保证；
 * 在 {@code Executor executor} 配置不正确的情形下，在Spring中可能存在优雅关闭的问题
 */
@Slf4j
public class AsyncEventExecutor implements EventExecutor {

    private final Executor executor;

    public AsyncEventExecutor(Executor asyncExecutor) {
        this.executor = asyncExecutor;
    }

    @Override
    public <E extends Event> void submit(Task<E> task) {
        executor.execute(task);
    }
}
