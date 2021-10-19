package com.github.lokic.dracula.eventbus.publisher;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.DefaultEventBus;
import com.github.lokic.dracula.eventbus.EventBus;
import com.github.lokic.dracula.eventbus.exchanger.Exchanger;
import com.github.lokic.dracula.eventbus.executor.EventExecutor;
import com.github.lokic.dracula.eventbus.handler.EventHandler;
import com.github.lokic.dracula.eventbus.transaction.JdbcTemplateExtension;
import com.github.lokic.dracula.eventbus.transaction.TransactionalEventManager;
import com.github.lokic.dracula.eventbus.transaction.mysql.TransactionalEventMysqlRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
public class TransactionalEventPublisherTest {

    @Test
    public void test_publish() {

        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .build();
        TransactionalEventMysqlRepository repository
                = new TransactionalEventMysqlRepository(new JdbcTemplateExtension(dataSource));

        Exchanger exchanger = new Exchanger();
        TransactionalEventManager manager = Mockito.spy(new TransactionalEventManager(repository, exchanger));
        PlatformTransactionManager tm = new DataSourceTransactionManager(dataSource);
        TransactionTemplate template = new TransactionTemplate(tm);

        TransactionalEventPublisher<TestEvent> queue = new TransactionalEventPublisher<>(TestEvent.class, manager);
        exchanger.bind(queue);

        EventBus eventBus = new DefaultEventBus(exchanger);
        TestEventHandler eventHandler = Mockito.spy(new TestEventHandler());
        eventBus.register(TestEvent.class, eventHandler, EventExecutor.SYNC);

        template.execute(status -> {
            eventBus.send(new TestEvent("1"));
            eventBus.send(new TestEvent("2"));

            // 事务没有提交，所以没有执行
            Mockito.verify(eventHandler, Mockito.never()).handle(Mockito.any(TestEvent.class));
            return null;
        });

        // 事务提交，执行成功
        Mockito.verify(eventHandler, Mockito.times(2)).handle(Mockito.any(TestEvent.class));
        Mockito.verify(manager, Mockito.times(1)).handleSuccess(Mockito.anyList());
    }


    public static class TestEvent extends Event {

        private final String content;

        public TestEvent(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }

    public static class TestEventHandler implements EventHandler<TestEvent> {

        @Override
        public void handle(TestEvent event) {

        }
    }

}