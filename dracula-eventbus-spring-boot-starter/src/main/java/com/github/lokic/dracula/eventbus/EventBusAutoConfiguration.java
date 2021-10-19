package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.eventbus.exchanger.Exchanger;
import com.github.lokic.dracula.eventbus.executor.EventExecutor;
import com.github.lokic.dracula.eventbus.executor.SyncEventExecutor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration
public class EventBusAutoConfiguration {

    @Bean
    public SyncEventExecutor syncEventExecutor() {
        return EventExecutor.SYNC;
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean
    public QueueBeanPostProcessor queueBeanPostProcessor() {
        return new QueueBeanPostProcessor();
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean
    public EventHandlerBeanPostProcessor eventHandlerBeanPostProcessor() {
        return new EventHandlerBeanPostProcessor();
    }


    @ConditionalOnMissingBean(Exchanger.class)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean
    public Exchanger exchanger() {
        return new Exchanger();
    }

}
