package com.github.lokic.dracula.eventbus;


import com.github.lokic.dracula.eventbus.executor.EventExecutor;
import com.github.lokic.dracula.eventbus.executor.SyncEventExecutor;
import com.github.lokic.dracula.eventbus.handler.EventHandler;
import com.github.lokic.dracula.eventbus.interceptor.Rule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author loki
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandlerComponent {

    /**
     * event handler使用的执行器，会从spring容器中取对应类型的bean，
     * 作为对应{@link EventHandler} 的 {@link EventExecutor}
     *
     * @return
     * @see com.github.lokic.dracula.eventbus.executor.SyncEventExecutor
     * @see com.github.lokic.dracula.eventbus.executor.AsyncEventExecutor
     */
    Class<? extends EventExecutor> executor() default SyncEventExecutor.class;

    /**
     * 拦截器的规则，见 {@link Rule}
     *
     * @return
     */
    String[] rules() default {};

}
