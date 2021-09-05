package com.github.lokic.dracula.eventbus.interceptors.internals;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.interceptors.Interceptor;
import com.github.lokic.dracula.eventbus.interceptors.InterceptorType;

/**
 * 内置拦截器
 *
 * @param <E>
 */
interface InternalInterceptor<E extends Event> extends Interceptor<E> {

    int DEFAULT_INTERNAL_INTERCEPTOR_ORDER = 0;

    @Override
    default InterceptorType getInterceptorType() {
        return InterceptorType.INTERNAL;
    }

    /**
     * 暂时设计成所有{@code InternalInterceptor}都是相同的order，排序在 {@link InternalInterceptorRegistry} 中完成
     *
     * @return
     */
    @Override
    default int getOrder() {
        return DEFAULT_INTERNAL_INTERCEPTOR_ORDER;
    }
}
