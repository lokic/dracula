package com.github.lokic.dracula.eventbus.transaction.config;

import com.github.lokic.dracula.eventbus.transaction.TransactionalEventManagement;
import com.github.lokic.dracula.eventbus.transaction.TransactionalEventRepository;
import com.github.lokic.dracula.eventbus.transaction.mysql.TransactionalEventMysqlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ManagementConfiguration {

    @Bean
    @ConditionalOnBean(JdbcTemplate.class)
    @ConditionalOnProperty(name = "dracula.event-bus.transaction.repository-mode", havingValue = "mysql")
    public TransactionalEventRepository transactionalEventRepository(JdbcTemplate jdbcTemplate) {
        return new TransactionalEventMysqlRepository(jdbcTemplate);
    }

    @Bean
    public TransactionalEventManagement transactionalEventManagement(TransactionalEventRepository repository) {
        return new TransactionalEventManagement(repository);
    }


}
