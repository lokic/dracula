package com.github.lokic.dracula.eventbus;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({ EventBusConfigRegistrar.class})
public @interface EnableEventBus {

    /**
     * event bus的类型，{@link EventBus}的实现，event bus默认会注册到spring容器中，不需要自行注册
     *
     * @return
     */
    Class<? extends EventBus> eventBus() default DefaultEventBus.class;
}
