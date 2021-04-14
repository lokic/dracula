package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.broker.DefaultBrokerManager;
import com.github.lokic.dracula.eventbus.executors.EventExecutor;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;
import com.github.lokic.dracula.eventbus.handlers.EventHandlerAttribute;
import com.github.lokic.dracula.eventbus.interceptors.Interceptor;
import com.github.lokic.dracula.eventbus.interceptors.InterceptorAttribute;
import com.github.lokic.dracula.eventbus.interceptors.extensions.ExtensionInterceptor;
import com.github.lokic.dracula.eventbus.interceptors.internals.InternalInterceptorRegistry;
import com.github.lokic.javaext.Types;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventBusConfigRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware, ResourceLoaderAware {

    /**
     * 对应 {@link EnableEventBus#eventBus()} 的名字
     */
    private static final String ANNOTATION_ATTRIBUTE_OF_EVENT_BUS = "eventBus";

    private BeanFactory beanFactory;

    private ResourceLoader resourceLoader;


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> attributes = getAnnotationAttributes(importingClassMetadata, EnableEventBus.class);
        if (attributes.containsKey(ANNOTATION_ATTRIBUTE_OF_EVENT_BUS)) {
            Object clazz = attributes.get(ANNOTATION_ATTRIBUTE_OF_EVENT_BUS);
            if (clazz instanceof Class) {
                addCommonPostProcessor(registry);

                Class<? extends EventBus> eventBusClazz = Types.cast(clazz);
                EventBus eventBus = getOrRegisterEventBusToSpring(registry, eventBusClazz);
                registerEventHandler(eventBus, importingClassMetadata, registry);
                registerPublisher(importingClassMetadata, registry);
            }

        }
    }

    private void addCommonPostProcessor(BeanDefinitionRegistry registry) {
        ((DefaultListableBeanFactory) registry).addBeanPostProcessor((BeanPostProcessor) beanFactory.getBean(AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
        ((DefaultListableBeanFactory) registry).addBeanPostProcessor((BeanPostProcessor) beanFactory.getBean(AnnotationConfigUtils.COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    private void registerEventHandler(EventBus eventBus, AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        EventHandlerScanner scanner = new EventHandlerScanner(registry);
        scanner.setResourceLoader(resourceLoader);

        Set<String> basePackages = getBasePackages(importingClassMetadata);
        scanner.doScan(basePackages.toArray(new String[0]));

        List<InterceptorAttribute<Event>> interceptorAttributes =
                Stream.of(getAllInternalInterceptors(), getAllExtensionInterceptors())
                        .flatMap(Function.identity())
                        .map(Types::<Interceptor<Event>>cast)
                        .map(InterceptorAttribute::new)
                        .collect(Collectors.toList());

        Map<String, EventHandler<?>> handlerMap = Types.cast(((ApplicationContext) resourceLoader).getBeansOfType(EventHandler.class));
        for (EventHandler<?> eventHandler : handlerMap.values()) {
            EventHandlerComponent eventHandlerComponent = AnnotationUtils.findAnnotation(eventHandler.getClass(), EventHandlerComponent.class);
            if (eventHandlerComponent != null) {
                EventHandlerAttribute attribute = buildEventHandlerComponentAttribute(registry, eventHandlerComponent);
                registerEventHandlerToEventBus(eventBus, eventHandler, interceptorAttributes, attribute);
            }
        }
    }


    @SuppressWarnings("unchecked")
    private void registerPublisher(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        PublisherScanner scanner = new PublisherScanner(registry);
        scanner.setResourceLoader(resourceLoader);
        Set<String> basePackages = getBasePackages(importingClassMetadata);
        scanner.doScan(basePackages.toArray(new String[0]));
    }


    private Stream<Interceptor<? extends Event>> getAllInternalInterceptors() {
        return InternalInterceptorRegistry.getAll().stream();
    }

    private Stream<Interceptor<? extends Event>> getAllExtensionInterceptors() {
        return ((ApplicationContext) resourceLoader).getBeansOfType(ExtensionInterceptor.class).values()
                .stream()
                .map(Types::cast);
    }

    private Map<String, Object> getAnnotationAttributes(AnnotationMetadata importingClassMetadata, Class<? extends Annotation> annotationClazz) {
        return Optional.ofNullable(importingClassMetadata.getAnnotationAttributes(annotationClazz.getName()))
                .orElseGet(HashMap::new);
    }


    /**
     * 生成basePackages
     */
    private Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Set<String> basePackages = new HashSet<>();
        basePackages.add(
                ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        return basePackages;
    }


    /**
     * 注册event bus到spring
     */
    private EventBus getOrRegisterEventBusToSpring(BeanDefinitionRegistry registry, Class<? extends EventBus> eventBusClazz) {
        registerToSpring(registry, DefaultBrokerManager.class);
        return registerToSpring(registry, eventBusClazz);
    }

    /**
     * 注册到spring
     *
     * @param registry
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> T registerToSpring(BeanDefinitionRegistry registry, Class<T> clazz) {
        if (!contains(clazz)) {
            RootBeanDefinition beanDefinition = new RootBeanDefinition();
            beanDefinition.setBeanClass(clazz);
            beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            beanDefinition.setPrimary(true);
            registry.registerBeanDefinition(clazz.getName(), beanDefinition);
        }
        return beanFactory.getBean(clazz);
    }


    /**
     * 注册event handler到event bus
     */
    @SuppressWarnings("unchecked")
    private void registerEventHandlerToEventBus(EventBus eventBus, EventHandler eventHandler, List<InterceptorAttribute<Event>> interceptorAttributes, EventHandlerAttribute attribute) {
        Class eventClazz = ResolvableType.forClass(eventHandler.getClass())
                .as(EventHandler.class)
                .getGeneric(0)
                .resolve();
        eventBus.register(eventClazz, eventHandler, interceptorAttributes, attribute);
    }

    private EventHandlerAttribute buildEventHandlerComponentAttribute(BeanDefinitionRegistry registry, EventHandlerComponent annotation) {
        return new EventHandlerAttribute(getOrCreateExecutor(registry, annotation.executor()), annotation.rules());
    }

    private EventExecutor getOrCreateExecutor(BeanDefinitionRegistry registry, Class<? extends EventExecutor> executorClazz) {
        if (!contains(executorClazz)) {
            RootBeanDefinition beanDefinition = new RootBeanDefinition();
            beanDefinition.setBeanClass(executorClazz);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            beanDefinition.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR);
            registry.registerBeanDefinition(executorClazz.getName(), beanDefinition);
        }
        return beanFactory.getBean(executorClazz);
    }

    private boolean contains(Class<?> executorClazz) {
        try {
            beanFactory.getBean(executorClazz);
            return true;
        } catch (NoSuchBeanDefinitionException e) {
            return false;
        }
    }

    private static class EventHandlerScanner extends ClassPathBeanDefinitionScanner {

        EventHandlerScanner(BeanDefinitionRegistry registry) {
            super(registry);
        }

        @Override
        protected void registerDefaultFilters() {
            addIncludeFilter(new AnnotationTypeFilter(EventHandlerComponent.class));
        }

        @Override
        protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
            return super.doScan(basePackages);
        }
    }


    private static class PublisherScanner extends ClassPathBeanDefinitionScanner {
        PublisherScanner(BeanDefinitionRegistry registry) {
            super(registry);
        }

        @Override
        protected void registerDefaultFilters() {
            addIncludeFilter(new AnnotationTypeFilter(PublisherComponent.class));
        }

        @Override
        protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
            return super.doScan(basePackages);
        }
    }


}
