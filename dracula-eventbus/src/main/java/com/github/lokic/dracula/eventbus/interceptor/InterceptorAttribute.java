package com.github.lokic.dracula.eventbus.interceptor;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.handler.EventHandlerAttribute;
import lombok.Getter;
import lombok.ToString;

/**
 * 拦截器属性的封装。
 *
 * @param <E>
 */
@ToString
@Getter
public class InterceptorAttribute<E extends Event> {

    private final String name;

    /**
     * 拦截器的rule，对应 {@link EventHandlerAttribute#getRules()} 中 {@link Rule}。
     * 每个内置拦截器有自己的名字，但是对外的rule都是统一是 {@link Rule#defaultRule()}。
     */
    private final Rule rule;

    private final int order;

    private final Interceptor<E> interceptor;

    private final InterceptorType type;

    public InterceptorAttribute(Interceptor<E> interceptor) {
        this.order = interceptor.getOrder();
        this.interceptor = interceptor;
        this.type = getType(interceptor);
        this.name = interceptor.getName();
        this.rule = isInternal() ? Rule.defaultRule() : Rule.of(getName());
    }

    private InterceptorType getType(Interceptor<E> interceptor) {
        InterceptorType type = interceptor.getInterceptorType();
        if (type != InterceptorType.EXTENSION && type != InterceptorType.INTERNAL) {
            throw new IllegalStateException("Interceptor name " + interceptor.getName() + " type not support");
        }
        return type;
    }

    private boolean isInternal() {
        return InterceptorType.INTERNAL == type;
    }

}
