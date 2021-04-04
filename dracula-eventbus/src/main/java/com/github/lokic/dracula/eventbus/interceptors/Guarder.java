package com.github.lokic.dracula.eventbus.interceptors;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handlers.HandlerContext;

/**
 * 守卫器，判断数据是否需要处理，(只针对查询做判断，不要执行一些写入和更新操作)
 * @param <E>
 */
public interface Guarder<E extends Event> extends Interceptor<E> {

    boolean onAccept(Event event, HandlerContext context);

    @Override
    default boolean onStart(Event event, HandlerContext context){
        return onAccept(event, context);
    }
}
