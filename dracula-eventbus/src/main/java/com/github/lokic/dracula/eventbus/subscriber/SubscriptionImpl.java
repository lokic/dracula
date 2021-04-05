package com.github.lokic.dracula.eventbus.subscriber;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.executors.EventExecutor;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;
import com.github.lokic.dracula.eventbus.handlers.EventHandlerAttribute;
import com.github.lokic.dracula.eventbus.interceptors.InterceptorAttribute;
import com.github.lokic.dracula.eventbus.interceptors.InterceptorChain;
import com.github.lokic.dracula.eventbus.interceptors.InterceptorChainImpl;
import com.github.lokic.dracula.eventbus.interceptors.internals.EventTypeInterceptor;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 处理一个事件 {@link Event}时 ，对应一个处理器 {@link EventHandler} 的执行相关的封装。
 * 包括 事件拦截器{@link InterceptorChain}、事件处理器 {@link EventHandler}，事件执行器{@link EventExecutor}
 *
 * @param <E>
 */
@Getter
public class SubscriptionImpl<E extends Event> implements Subscription<E> {

    @NonNull
    private final InterceptorChain<E> chain;

    @NonNull
    private final EventHandler<E> handler;

    @NonNull
    private final EventExecutor executor;

    public SubscriptionImpl(Class<E> eventClazz, EventHandler<E> handler, List<InterceptorAttribute<E>> interceptorAttributes, EventHandlerAttribute eventHandlerAttribute) {
        List<InterceptorAttribute<E>> interceptorAttrs = Optional.ofNullable(interceptorAttributes)
                .orElseGet(ArrayList::new);

        // 强制第一个必须是事件类型的拦截器
        interceptorAttrs.add(0, new InterceptorAttribute<>(new EventTypeInterceptor<>(eventClazz)));

        this.chain = new InterceptorChainImpl<>(eventClazz, interceptorAttrs, eventHandlerAttribute.getRules());
        this.handler = handler;
        this.executor = eventHandlerAttribute.getExecutor();
    }

    @Override
    public void process(E event) {
        getExecutor().submit(getChain(), getHandler(), event);
    }

}
