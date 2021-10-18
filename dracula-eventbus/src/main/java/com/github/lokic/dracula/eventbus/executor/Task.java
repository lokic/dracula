package com.github.lokic.dracula.eventbus.executor;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handler.EventHandler;
import com.github.lokic.dracula.eventbus.handler.HandlerContext;
import com.github.lokic.dracula.eventbus.handler.HandlerContextImpl;
import com.github.lokic.dracula.eventbus.interceptor.InterceptorChain;
import com.github.lokic.dracula.eventbus.interceptor.InterceptorExecution;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * 在 {@link EventExecutor} 中执行的任务
 *
 * @param <E>
 */
@Slf4j
@Value
public class Task<E extends Event> implements Runnable {

    @NonNull
    private final InterceptorChain<E> chain;

    @NonNull
    private final EventHandler<E> handler;

    @NonNull
    private final E event;

    @Override
    public void run() {
        try {
            HandlerContext context = new HandlerContextImpl();
            InterceptorExecution<? extends Event> interceptorExecution = new InterceptorExecution<>(chain.getInterceptors());
            if (interceptorExecution.applyOnStart(event, context)) {
                try {
                    this.handler.handle(event);
                    interceptorExecution.applyOnSuccess(event, context);
                } catch (Exception e) {
                    interceptorExecution.applyOnException(event, context, e);
                    throw e;
                } finally {
                    interceptorExecution.applyOnFinal(event, context);
                }
            }
        } catch (Exception e) {
            log.error("failed to handle event {} use {}", this.event, this.handler, e);
        }
    }
}
