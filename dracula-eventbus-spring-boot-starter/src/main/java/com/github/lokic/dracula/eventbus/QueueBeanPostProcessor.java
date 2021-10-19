package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.annotation.PublisherComponent;
import com.github.lokic.dracula.eventbus.annotation.SubscriberComponent;
import com.github.lokic.dracula.eventbus.exchanger.Exchanger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;

public class QueueBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements BeanFactoryAware {

    private Exchanger exchanger;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (beanFactory instanceof DefaultListableBeanFactory) {
            this.exchanger = beanFactory.getBean(Exchanger.class);
        }
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (isPublisherComponent(bean)) {
            exchanger.bind((Publisher<? extends Event>) bean);
        }
        if (isSubscriberComponent(bean)) {
            exchanger.bind((Subscriber<? extends Event>) bean);
        }
        return bean;
    }

    private boolean isPublisherComponent(Object bean) {
        return bean instanceof Publisher
                && (AnnotationUtils.findAnnotation(bean.getClass(), PublisherComponent.class) != null);
    }

    private boolean isSubscriberComponent(Object bean) {
        return bean instanceof Subscriber
                && (AnnotationUtils.findAnnotation(bean.getClass(), SubscriberComponent.class) != null);
    }

}