package com.github.lokic.dracula.eventbus.interceptors;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;
import com.github.lokic.dracula.eventbus.handlers.HandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 拦截器执行时的切面，封装了拦截器执行的顺序、执行到哪个拦截器等。
 * <p>
 * {@link EventHandler} 执行之后，
 * {@link #applyOnSuccess(Event, HandlerContext)}和{@link #applyOnException(Event, HandlerContext, Throwable)}
 * 只会执行其中一个：
 * 如果执行成功，则调用 {@link #applyOnSuccess(Event, HandlerContext)}
 * 如果执行失败，则调用 {@link #applyOnException(Event, HandlerContext, Throwable)}
 * <p>
 * <p>
 * 方法 @link #applyOnFinal(Event, HandlerContext)}在下面这些方法执行之后都会执行：
 * <ul>
 *     <li>{@link #applyOnStart(Event, HandlerContext)}</li>
 *     <li>{@link #applyOnSuccess(Event, HandlerContext)}</li>
 *     <li>{@link #applyOnException(Event, HandlerContext, Throwable)}</li>
 * </ul>
 * <p>
 *
 * @param <E>
 */
@Slf4j
public class InterceptorExecution<E extends Event> {

    /**
     * 记录下拦截器执行到的下标
     */
    private int interceptorIndex = -1;

    private final List<Interceptor<E>> interceptors;

    public InterceptorExecution(List<Interceptor<E>> interceptors) {
        this.interceptors = interceptors;
    }

    public boolean applyOnStart(Event event, HandlerContext context) {
        for (int i = 0; i < interceptors.size(); i++) {
            Interceptor<? extends Event> interceptor = interceptors.get(i);
            if (!interceptor.onStart(event, context)) {
                applyOnFinal(event, context);
                return false;
            }
            interceptorIndex = i;
        }
        return true;
    }

    public void applyOnSuccess(Event event, HandlerContext context) {
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            Interceptor<? extends Event> interceptor = interceptors.get(i);
            try {
                interceptor.onSuccess(event, context);
            } catch (Exception e) {
                log.error("interceptor = {}, onSuccess error", interceptor, e);
            }
        }
    }

    public void applyOnException(Event event, HandlerContext context, Throwable exception) {
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            Interceptor<? extends Event> interceptor = interceptors.get(i);
            try {
                interceptor.onException(event, context, exception);
            } catch (Exception e) {
                log.error("interceptor = {}, onException error", interceptor, e);
            }
        }
    }

    public void applyOnFinal(Event event, HandlerContext context) {
        for (int i = interceptorIndex; i >= 0; i--) {
            Interceptor<? extends Event> interceptor = interceptors.get(i);
            try {
                interceptor.onFinal(event, context);
            } catch (Exception e) {
                log.error("interceptor = {}, onFinal error", interceptor, e);
            }
        }
    }
}
