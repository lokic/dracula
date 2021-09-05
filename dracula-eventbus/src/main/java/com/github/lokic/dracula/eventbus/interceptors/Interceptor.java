package com.github.lokic.dracula.eventbus.interceptors;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handlers.HandlerContext;

public interface Interceptor<E extends Event> extends Named, Typed, Ordered {

    /**
     * 开始的时候执行
     *
     * @param event
     * @param context
     * @return
     */
    default boolean onStart(Event event, HandlerContext context) {
        return false;
    }

    /**
     * 成功之后执行
     *
     * @param event
     * @param context
     */
    default void onSuccess(Event event, HandlerContext context) {

    }

    /**
     * 异常之后执行
     *
     * @param event
     * @param context
     * @param exception
     */
    default void onException(Event event, HandlerContext context, Throwable exception) {

    }

    /**
     * 完成之后执行
     *
     * @param event
     * @param context
     */
    default void onFinal(Event event, HandlerContext context) {

    }


}
