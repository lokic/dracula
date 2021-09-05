package com.github.lokic.dracula.eventbus.interceptors.internals;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handlers.HandlerContext;
import com.github.lokic.dracula.eventbus.interceptors.Guarder;

/**
 * 判断事件的类型是否准入，默认作为第一个内置拦截器强制导入
 *
 * @param <E>
 */
public class EventTypeInterceptor<E extends Event> implements InternalInterceptor<E>, Guarder<E> {

    public static final String NAME = "eventType";

    private final Class<E> eventClazz;

    public EventTypeInterceptor(Class<E> eventClazz) {
        this.eventClazz = eventClazz;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean onAccept(Event event, HandlerContext context) {
        return event.getClass() == eventClazz;
    }

}
