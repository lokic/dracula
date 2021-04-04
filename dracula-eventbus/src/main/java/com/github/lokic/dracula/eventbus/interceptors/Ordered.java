package com.github.lokic.dracula.eventbus.interceptors;

import com.github.lokic.dracula.eventbus.interceptors.internals.InternalInterceptorRegistry;

/**
 * 可排序的
 */
public interface Ordered {

    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    /**
     * 拦截器的顺序
     *
     * {@code com.github.lokic.dracula.eventbus.interceptors.internals.InternalInterceptor} 相关子类的顺序是在
     * {@link InternalInterceptorRegistry} 中调整的。
     *
     * @return
     */
    int getOrder();
}
