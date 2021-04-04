package com.github.lokic.dracula.eventbus.interceptors;

import com.github.lokic.dracula.event.Event;

import java.util.List;

/**
 * 拦截器链，对一个处理器的相关拦截器，及拦截器顺序的封装
 *
 * @param <E>
 */
public interface InterceptorChain<E extends Event> {

    List<Interceptor<E>> getInterceptors();

}
