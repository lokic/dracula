package com.github.lokic.dracula.eventbus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 如果多个Subscriber有顺序要求可以使用{@link org.springframework.context.annotation.DependsOn}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SubscriberComponent {
}
