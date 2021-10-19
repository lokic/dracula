package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.eventbus.annotation.EnableEventBus;
import com.github.lokic.dracula.eventbus.annotation.EventHandlerComponent;
import com.github.lokic.dracula.eventbus.annotation.PublisherComponent;
import com.github.lokic.dracula.eventbus.annotation.SubscriberComponent;
import com.github.lokic.javaplus.Types;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.*;

public class EventBusConfigRegistrar implements ImportBeanDefinitionRegistrar {

    /**
     * 对应 {@link EnableEventBus#eventBus()} 的名字
     */
    private static final String ANNOTATION_ATTRIBUTE_OF_EVENT_BUS = "eventBus";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> attributes = getAnnotationAttributes(importingClassMetadata, EnableEventBus.class);
        if (attributes.containsKey(ANNOTATION_ATTRIBUTE_OF_EVENT_BUS)) {
            Object eventBusClazz = attributes.get(ANNOTATION_ATTRIBUTE_OF_EVENT_BUS);
            if (eventBusClazz instanceof Class) {
                registerToSpring(registry, Types.cast(eventBusClazz));
                Scanner.doScan(importingClassMetadata, registry, PublisherComponent.class, SubscriberComponent.class);
                Scanner.doScan(importingClassMetadata, registry, EventHandlerComponent.class);
            }
        }
    }

    private Map<String, Object> getAnnotationAttributes(AnnotationMetadata importingClassMetadata, Class<? extends Annotation> annotationClazz) {
        return Optional.ofNullable(importingClassMetadata.getAnnotationAttributes(annotationClazz.getName()))
                .orElseGet(HashMap::new);
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

    private static abstract class Scanner extends ClassPathBeanDefinitionScanner {

        @SafeVarargs
        public static void doScan(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, Class<? extends Annotation>... customIncludeFilters) {
            Scanner scanner = new Scanner(registry) {
                @Override
                List<Class<? extends Annotation>> getCustomIncludeFilters() {
                    return Lists.newArrayList(customIncludeFilters);
                }
            };
            Set<String> basePackages = getBasePackages(importingClassMetadata);
            scanner.doScan(basePackages.toArray(new String[0]));
        }


        /**
         * 获取basePackages
         */
        private static Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
            Set<String> basePackages = new HashSet<>();
            basePackages.add(
                    ClassUtils.getPackageName(importingClassMetadata.getClassName()));
            return basePackages;
        }

        Scanner(BeanDefinitionRegistry registry) {
            super(registry);
        }

        @Override
        protected void registerDefaultFilters() {
            addIncludeFilters(getCustomIncludeFilters());
        }

        abstract List<Class<? extends Annotation>> getCustomIncludeFilters();

        final void addIncludeFilters(List<Class<? extends Annotation>> annotationTypes) {
            for (Class<? extends Annotation> annotationType : annotationTypes) {
                addIncludeFilter(new AnnotationTypeFilter(annotationType));
            }
        }

        @Override
        public Set<BeanDefinitionHolder> doScan(String... basePackages) {
            return super.doScan(basePackages);
        }

    }


}
