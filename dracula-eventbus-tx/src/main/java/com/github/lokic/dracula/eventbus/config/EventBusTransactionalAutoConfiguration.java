package com.github.lokic.dracula.eventbus.config;

import com.github.lokic.dracula.eventbus.exchanger.Exchanger;
import com.github.lokic.dracula.eventbus.transaction.JdbcTemplateExtension;
import com.github.lokic.dracula.eventbus.transaction.TransactionalEventManager;
import com.github.lokic.dracula.eventbus.transaction.TransactionalEventRepository;
import com.github.lokic.dracula.eventbus.transaction.mysql.TransactionalEventMysqlRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class EventBusTransactionalAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "dracula.event-bus.transaction.repository-mode", havingValue = "mysql")
    public TransactionalEventRepository transactionalEventRepository(DataSource dataSource) {
        return new TransactionalEventMysqlRepository(new JdbcTemplateExtension(dataSource));
    }

    @Bean
    public TransactionalEventManager transactionalEventManagement(TransactionalEventRepository repository, Exchanger exchanger) {
        return new TransactionalEventManager(repository, exchanger);
    }

    @Bean
    @ConditionalOnBean(TransactionalEventManager.class)
    @ConditionalOnProperty(name = "dracula.event-bus.transaction.publish-mode", havingValue = "scheduling")
    public int initScheduling(TransactionalEventManager transactionalEventManager) {
        transactionalEventManager.init();
        return 1;
    }

}