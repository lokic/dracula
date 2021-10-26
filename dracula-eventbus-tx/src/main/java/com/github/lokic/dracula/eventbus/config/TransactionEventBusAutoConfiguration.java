package com.github.lokic.dracula.eventbus.config;

import com.github.lokic.dracula.eventbus.exchanger.Exchanger;
import com.github.lokic.dracula.eventbus.lock.DistributedLockerFactory;
import com.github.lokic.dracula.eventbus.lock.db.DbDistributedLockerFactory;
import com.github.lokic.dracula.eventbus.lock.db.LockRepository;
import com.github.lokic.dracula.eventbus.transaction.EventKeyParser;
import com.github.lokic.dracula.eventbus.transaction.JdbcTemplateExtension;
import com.github.lokic.dracula.eventbus.transaction.TransactionEventManager;
import com.github.lokic.dracula.eventbus.transaction.TransactionEventRepository;
import com.github.lokic.dracula.eventbus.transaction.mysql.TransactionEventMysqlRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class TransactionEventBusAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "dracula.event-bus.transaction.repository", havingValue = "mysql")
    public TransactionEventRepository transactionEventRepository(DataSource dataSource) {
        return new TransactionEventMysqlRepository(new JdbcTemplateExtension(dataSource));
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
    public TransactionEventManager transactionEventManagement(TransactionEventRepository repository, Exchanger exchanger, DistributedLockerFactory distributedLockerFactory) {
        return new TransactionEventManager(repository, exchanger, eventKeyMapping(), distributedLockerFactory);
    }

    @Bean
    @ConditionalOnBean(TransactionEventManager.class)
    @ConditionalOnProperty(name = "dracula.event-bus.transaction.publish", havingValue = "scheduling")
    public int initScheduling(TransactionEventManager transactionEventManager) {
        transactionEventManager.init();
        return 1;
    }

}
