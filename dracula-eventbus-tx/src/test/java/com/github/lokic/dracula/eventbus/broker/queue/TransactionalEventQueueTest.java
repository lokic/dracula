package com.github.lokic.dracula.eventbus.broker.queue;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.transaction.JdbcTemplateExtension;
import com.github.lokic.dracula.eventbus.transaction.TransactionalEventManager;
import com.github.lokic.dracula.eventbus.transaction.TransactionalEventRepository;
import com.github.lokic.dracula.eventbus.transaction.mysql.TransactionalEventMysqlRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
public class TransactionalEventQueueTest {





    @Test
    public void test_publish() {

        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .build();
        TransactionalEventMysqlRepository repository
                = new TransactionalEventMysqlRepository(new JdbcTemplateExtension(dataSource));

        TransactionalEventManager manager = new TransactionalEventManager(repository);
        PlatformTransactionManager tm = new DataSourceTransactionManager(dataSource);
        TransactionTemplate template = new TransactionTemplate(tm);

        template.execute(status -> {
            TransactionalEventQueue<TestEvent> queue = new TransactionalEventQueue<>(TestEvent.class, manager);
            queue.publish(new TestEvent("1"));
            queue.publish(new TestEvent("2"));
            return null;
        });

    }

    public static class TestEvent extends Event {

        private String content;

        public TestEvent(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }

}