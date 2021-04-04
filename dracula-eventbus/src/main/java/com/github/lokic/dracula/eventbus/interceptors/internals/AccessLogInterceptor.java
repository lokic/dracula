package com.github.lokic.dracula.eventbus.interceptors.internals;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handlers.HandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccessLogInterceptor<E extends Event> implements InternalInterceptor<E> {

    private static final String NAME = "accessLog";

    @Override
    public boolean onStart(Event event, HandlerContext context) {
        log.info("onStart event={}, context={}", event, context);
        return true;
    }

    @Override
    public void onFinal(Event event, HandlerContext context) {
        log.info("onFinal event={}, context={}", event, context);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
