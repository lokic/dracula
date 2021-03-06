package com.github.lokic.dracula.eventbus.subscriber;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.Subscription;
import com.github.lokic.dracula.eventbus.executor.EventExecutor;
import com.github.lokic.dracula.eventbus.handler.EventHandler;
import com.github.lokic.dracula.eventbus.handler.EventHandlerAttribute;
import com.github.lokic.dracula.eventbus.interceptor.InterceptorAttribute;
import com.github.lokic.dracula.eventbus.interceptor.InterceptorChain;
import com.github.lokic.dracula.eventbus.interceptor.InterceptorChainImpl;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

/**
 * 处理一个事件 {@link Event}时 ，对应一个处理器 {@link EventHandler} 的执行相关的封装。
 * 包括 事件拦截器{@link InterceptorChain}、事件处理器 {@link EventHandler}，事件执行器{@link EventExecutor}
 *
 * @param <E>
 */
@Getter
public final class DefaultSubscription<E extends Event> implements Subscription<E> {

    @NonNull
    private final InterceptorChain<E> chain;

    @NonNull
    private final EventHandler<E> handler;

    @NonNull
    private final EventExecutor executor;

    @NonNull
    private final Class<E> genericType;

    public DefaultSubscription(@NonNull Class<E> eventClazz, @NonNull EventHandler<E> handler, List<InterceptorAttribute<? extends E>> interceptorAttributes, EventHandlerAttribute eventHandlerAttribute) {
        this.genericType = eventClazz;
        this.chain = new InterceptorChainImpl<>(eventClazz, interceptorAttributes, eventHandlerAttribute.getRules());
        this.handler = handler;
        this.executor = eventHandlerAttribute.getExecutor();
    }

    @Override
    public void process(E event) {
        getExecutor().submit(getChain(), getHandler(), event);
    }

}
