package com.github.lokic.dracula.eventbus.interceptor.internal;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.interceptor.Interceptor;
import com.github.lokic.dracula.eventbus.interceptor.InterceptorType;

/**
 * 内置拦截器
 *
 * @param <E>
 */
interface InternalInterceptor<E extends Event> extends Interceptor<E> {

    @Override
    default InterceptorType getInterceptorType() {
        return InterceptorType.INTERNAL;
    }

}
