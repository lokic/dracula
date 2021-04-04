package com.github.lokic.dracula.eventbus.interceptors.internals;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.interceptors.Interceptor;
import com.github.lokic.dracula.eventbus.interceptors.InterceptorType;

import java.lang.reflect.Type;
import java.util.*;

/**
 * 内置拦截器的注册器，提供所有内置拦截器的查询。
 * 如果有新加内置拦截器，都需要在这个里面配置；内部拦截器的顺序在这个里面调整
 * <p>
 */
public class InternalInterceptorRegistry {

    private static final Map<Type, Interceptor<? extends Event>> INTERCEPTORS = new LinkedHashMap<>();

    static {
        register(new AccessLogInterceptor<>());
    }

    private static void register(Interceptor<? extends Event> interceptor){
        if(interceptor.getInterceptorType() == InterceptorType.INTERNAL) {
            INTERCEPTORS.put(interceptor.getClass(), interceptor);
        }
        // TODO throw exception？
    }

    public static List<Interceptor<? extends Event>> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(INTERCEPTORS.values()));
    }
}
