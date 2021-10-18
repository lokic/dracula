package com.github.lokic.dracula.eventbus.executor;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handler.EventHandler;
import com.github.lokic.dracula.eventbus.interceptor.InterceptorChain;

/**
 * 事件执行器，包括同步和异步二类执行器
 *
 * @see com.github.lokic.dracula.eventbus.executor.SyncEventExecutor
 * @see com.github.lokic.dracula.eventbus.executor.AsyncEventExecutor
 */
public interface EventExecutor {

    SyncEventExecutor SYNC = new SyncEventExecutor();

    default <E extends Event> void submit(InterceptorChain<E> chain, EventHandler<E> handler, E event) {
        submit(new Task<>(chain, handler, event));
    }

    <E extends Event> void submit(Task<E> task);

}
