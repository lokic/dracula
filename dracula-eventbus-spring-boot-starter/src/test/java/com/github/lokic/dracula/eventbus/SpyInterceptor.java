package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.event.TestEvent;
import com.github.lokic.dracula.eventbus.handler.HandlerContext;
import com.github.lokic.dracula.eventbus.interceptor.extension.ExtensionInterceptor;

public class SpyInterceptor implements ExtensionInterceptor<TestEvent> {
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

    @Override
    public String getName() {
        return "testInterceptor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
