package com.github.lokic.dracula.eventbus.interceptor.internal;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.interceptor.Interceptor;
import com.github.lokic.dracula.eventbus.interceptor.InterceptorType;

import java.lang.reflect.Type;
import java.util.*;

/**
 * 内置拦截器的注册器，提供所有内置拦截器的查询。
 * 如果有新加内置拦截器，都需要在这个里面配置；内部拦截器的顺序在这个里面调整
 * <p>
 */
public class InternalInterceptorRegistry {

    public static final InternalInterceptorRegistry INSTANCE = new InternalInterceptorRegistry();

    private final Map<Type, Interceptor<? extends Event>> INTERCEPTORS = new LinkedHashMap<>();

    private InternalInterceptorRegistry() {
        register(new AccessLogInterceptor<>());
    }

    private void register(Interceptor<? extends Event> interceptor) {
        if (interceptor.getInterceptorType() != InterceptorType.INTERNAL) {
            throw new IllegalArgumentException("only internal interceptor can be registered, interceptor = " + interceptor.getName());
        }
        INTERCEPTORS.put(interceptor.getClass(), interceptor);
    }

    public List<Interceptor<? extends Event>> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(INTERCEPTORS.values()));
    }
}
