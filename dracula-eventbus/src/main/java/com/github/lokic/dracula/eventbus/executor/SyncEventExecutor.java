package com.github.lokic.dracula.eventbus.executor;

import com.github.lokic.dracula.event.Event;

/**
 * 同步事件执行器，事件分发和事件处理在同一个线程上下文内完成。
 * 适合一些单纯只需要通过EventBus来解耦的场景，但是又希望在一个事务内完成，又对性能没有特别高要求的场景。
 */
public class SyncEventExecutor implements EventExecutor {

    @Override
    public <E extends Event> void submit(Task<E> task) {
        task.run();
    }
}
