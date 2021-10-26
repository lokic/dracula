package com.github.lokic.dracula.eventbus.config;

import com.github.lokic.dracula.eventbus.exchanger.Exchanger;
import com.github.lokic.dracula.eventbus.lock.DistributedLockerFactory;
import com.github.lokic.dracula.eventbus.lock.db.DbDistributedLockerFactory;
import com.github.lokic.dracula.eventbus.lock.db.LockRepository;
import com.github.lokic.dracula.eventbus.transaction.EventKeyParser;
import com.github.lokic.dracula.eventbus.transaction.JdbcTemplateExtension;
import com.github.lokic.dracula.eventbus.transaction.TransactionalEventManager;
import com.github.lokic.dracula.eventbus.transaction.TransactionalEventRepository;
import com.github.lokic.dracula.eventbus.transaction.mysql.TransactionalEventMysqlRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class TransactionalEventBusAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "dracula.event-bus.transaction.repository-mode", havingValue = "mysql")
    public TransactionalEventRepository transactionalEventRepository(DataSource dataSource) {
        return new TransactionalEventMysqlRepository(new JdbcTemplateExtension(dataSource));
    }

    @Bean
    public EventKeyParser eventKeyMapping() {
        return new EventKeyParser();
    }

    @Bean
    @ConditionalOnMissingBean(DistributedLockerFactory.class)
    public DistributedLockerFactory distributedLockerFactory(JdbcTemplate jdbcTemplate) {
        return new DbDistributedLockerFactory(new LockRepository(jdbcTemplate));
    }

    @Bean
    public TransactionalEventManager transactionalEventManagement(TransactionalEventRepository repository, Exchanger exchanger, DistributedLockerFactory distributedLockerFactory) {
        return new TransactionalEventManager(repository, exchanger, eventKeyMapping(), distributedLockerFactory);
    }

    @Bean
    @ConditionalOnBean(TransactionalEventManager.class)
    @ConditionalOnProperty(name = "dracula.event-bus.transaction.publish-mode", havingValue = "scheduling")
    public int initScheduling(TransactionalEventManager transactionalEventManager) {
        transactionalEventManager.init();
        return 1;
    }

}
