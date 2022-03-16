package com.github.lokic.dracula.eventbus;

import com.github.lokic.custom.registrar.AnnotationAttributeUtils;
import com.github.lokic.custom.registrar.Scanner;
import com.github.lokic.dracula.eventbus.annotation.EnableEventBus;
import com.github.lokic.dracula.eventbus.annotation.EventHandlerComponent;
import com.github.lokic.dracula.eventbus.annotation.PublisherComponent;
import com.github.lokic.dracula.eventbus.annotation.SubscriberComponent;
import com.github.lokic.javaplus.Types;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventBusConfigRegistrar implements ImportBeanDefinitionRegistrar {

    /**
     * 对应 {@link EnableEventBus#eventBus()} 的名字
     */
    private static final String EVENT_BUS_ATTRIBUTE_NAME = "eventBus";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> attributes = AnnotationAttributeUtils.getAnnotationAttributes(importingClassMetadata, EnableEventBus.class);
        if (attributes.containsKey(EVENT_BUS_ATTRIBUTE_NAME)) {
            Object eventBusClazz = attributes.get(EVENT_BUS_ATTRIBUTE_NAME);
            if (eventBusClazz instanceof Class) {
                registerToSpring(registry, Types.cast(eventBusClazz));

                Scanner scanner = new Scanner(importingClassMetadata, registry, EnableEventBus.class) {
                    @Override
                    protected List<Class<? extends Annotation>> getCustomIncludeFilters() {
                        return Stream.of(PublisherComponent.class, SubscriberComponent.class, EventHandlerComponent.class)
                                .collect(Collectors.toList());
                    }
                };
                scanner.doScan();
            }
        }
    }

    /**
     * 注册到spring
     *
     * @param registry
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> void registerToSpring(BeanDefinitionRegistry registry, Class<T> clazz) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(clazz);
        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
        beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        beanDefinition.setPrimary(true);
        registry.registerBeanDefinition(clazz.getName(), beanDefinition);
    }
}
