package com.github.lokic.dracula.eventbus.interceptor.internal;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handler.HandlerContext;
import lombok.ToString;

@ToString
public class SpyInternalInterceptor<E extends Event> implements InternalInterceptor<E> {

    private final String name;
    private final int order;

    public SpyInternalInterceptor(String name, int order) {
        this.name = name;
        this.order = order;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void onSuccess(Event event, HandlerContext context) {

    }

    @Override
    public void onException(Event event, HandlerContext context, Throwable exception) {

    }

    @Override
    public void onFinal(Event event, HandlerContext context) {

    }

    @Override
    public int getOrder() {
        return order;
    }
}
