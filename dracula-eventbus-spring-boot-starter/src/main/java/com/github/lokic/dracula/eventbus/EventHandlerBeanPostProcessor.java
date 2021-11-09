package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.annotation.EventHandlerComponent;
import com.github.lokic.dracula.eventbus.handler.EventHandler;
import com.github.lokic.dracula.eventbus.handler.EventHandlerAttribute;
import com.github.lokic.dracula.eventbus.interceptor.Interceptor;
import com.github.lokic.dracula.eventbus.interceptor.InterceptorAttribute;
import com.github.lokic.dracula.eventbus.interceptor.extension.ExtensionInterceptor;
import com.github.lokic.dracula.eventbus.interceptor.extension.ExtensionInterceptorRegistry;
import com.github.lokic.dracula.eventbus.interceptor.internal.InternalInterceptorRegistry;
import com.github.lokic.javaplus.Types;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventHandlerBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private InterceptorManager interceptorManager;

    private EventBus eventBus;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.interceptorManager = new InterceptorManager(applicationContext);
        this.eventBus = applicationContext.getBean(EventBus.class);
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof EventHandler) {
            EventHandlerComponent eventHandlerComponent = AnnotationUtils.findAnnotation(bean.getClass(), EventHandlerComponent.class);
            if (eventHandlerComponent != null) {
                @SuppressWarnings("unchecked")
                EventHandler<Event> eventHandler = (EventHandler<Event>) bean;
                List<InterceptorAttribute<? extends Event>> allInterceptorAttributes = interceptorManager.getAllInterceptorAttributes();

                EventHandlerAttribute attribute = buildEventHandlerComponentAttribute(eventHandlerComponent);
                eventBus.register(eventHandler, allInterceptorAttributes, attribute);
            }
        }
        return bean;
    }

    private EventHandlerAttribute buildEventHandlerComponentAttribute(EventHandlerComponent annotation) {
        return new EventHandlerAttribute(applicationContext.getBean(annotation.executor()), Arrays.stream(annotation.rules()).collect(Collectors.toList()));
    }

    private static class InterceptorManager {

        private final ExtensionInterceptorRegistry extensionInterceptorRegistry;
        private final InternalInterceptorRegistry internalInterceptorRegistry;

        public InterceptorManager(ApplicationContext applicationContext) {
            this.extensionInterceptorRegistry = new ApplicationContextExtensionInterceptorRegistry(applicationContext);
            this.internalInterceptorRegistry = InternalInterceptorRegistry.INSTANCE;
        }

        public List<Interceptor<? extends Event>> getAllInterceptors() {
            return Stream.of(internalInterceptorRegistry.getAll().stream(), extensionInterceptorRegistry.getAll().stream())
                    .flatMap(Function.identity())
                    .collect(Collectors.toList());
        }

        public List<InterceptorAttribute<? extends Event>> getAllInterceptorAttributes() {
            return getAllInterceptors()
                    .stream()
                    .map(InterceptorAttribute::new)
                    .collect(Collectors.toList());
        }
    }

    private static class ApplicationContextExtensionInterceptorRegistry implements ExtensionInterceptorRegistry {

        private final ApplicationContext applicationContext;

        public ApplicationContextExtensionInterceptorRegistry(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override
        public List<Interceptor<? extends Event>> getAll() {
            return applicationContext.getBeansOfType(ExtensionInterceptor.class).values()
                    .stream()
                    .map(Types::<Interceptor<? extends Event>>cast)
                    .collect(Collectors.toList());
        }
    }
}
