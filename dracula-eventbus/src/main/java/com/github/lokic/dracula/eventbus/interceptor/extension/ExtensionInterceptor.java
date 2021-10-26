package com.github.lokic.dracula.eventbus.interceptor.extension;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.interceptor.Interceptor;
import com.github.lokic.dracula.eventbus.interceptor.InterceptorType;

/**
 * 扩展拦截器，给业务应用使用
 * <p>
 *
 * @param <E>
 */
public interface ExtensionInterceptor<E extends Event> extends Interceptor<E> {

    @Override
    default InterceptorType getInterceptorType() {
        return InterceptorType.EXTENSION;
    }
}
