package com.github.lokic.dracula.eventbus.interceptor.extension;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handler.HandlerContext;
import lombok.ToString;

@ToString
public class StubExtensionInterceptor<E extends Event> implements ExtensionInterceptor<E> {
    private final String name;
    private final int order;

    public StubExtensionInterceptor(String name, int order) {
        this.name = name;
        this.order = order;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public boolean onStart(Event event, HandlerContext context) {
        return false;
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
}
