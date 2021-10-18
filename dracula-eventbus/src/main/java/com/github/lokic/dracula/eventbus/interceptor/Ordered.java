package com.github.lokic.dracula.eventbus.interceptor;

import com.github.lokic.dracula.eventbus.interceptor.internal.InternalInterceptorRegistry;

/**
 * 可排序的
 */
public interface Ordered {

    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    /**
     * 拦截器的顺序
     * <p>
     * {@code com.github.lokic.dracula.eventbus.interceptor.internal.InternalInterceptor} 相关子类的顺序是在
     * {@link InternalInterceptorRegistry} 中调整的。
     *
     * @return
     */
    int getOrder();
}
