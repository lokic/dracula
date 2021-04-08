package com.github.lokic.dracula.eventbus.transaction.config;

import com.github.lokic.dracula.eventbus.transaction.mysql.JdbcTemplateExtension;
import com.github.lokic.dracula.eventbus.transaction.TransactionalEventManagement;
import com.github.lokic.dracula.eventbus.transaction.TransactionalEventRepository;
import com.github.lokic.dracula.eventbus.transaction.mysql.TransactionalEventMysqlRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class ManagementConfiguration {

    @Bean
    @ConditionalOnBean(JdbcTemplate.class)
    @ConditionalOnProperty(name = "dracula.event-bus.transaction.repository-mode", havingValue = "mysql")
    public TransactionalEventRepository transactionalEventRepository(DataSource dataSource){
        return new TransactionalEventMysqlRepository(new JdbcTemplateExtension(dataSource));
    }

    @Bean
    public TransactionalEventManagement transactionalEventManagement(TransactionalEventRepository repository) {
        return new TransactionalEventManagement(repository);
    }


}
