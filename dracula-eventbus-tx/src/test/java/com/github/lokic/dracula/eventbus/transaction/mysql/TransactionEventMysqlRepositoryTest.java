package com.github.lokic.dracula.eventbus.transaction.mysql;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.transaction.JdbcTemplateExtension;
import com.github.lokic.dracula.eventbus.transaction.TransactionEvent;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;


public class TransactionEventMysqlRepositoryTest {

    @Test
    public void test_queryEvents() {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .addScript("classpath:data.sql")
                .build();
        TransactionEventMysqlRepository repository = new TransactionEventMysqlRepository(new JdbcTemplateExtension(dataSource));
        List<TransactionEvent<? extends Event>> list = repository.queryEvents(
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                LocalDateTime.of(2021, 2, 1, 0, 0, 0),
                100);
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void test_save() {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .build();
        TransactionEventMysqlRepository repository = new TransactionEventMysqlRepository(new JdbcTemplateExtension(dataSource));

        TransactionEvent<TestEvent> txEvent = new TransactionEvent<>();
        txEvent.setInitBackoff(10L);
        txEvent.setBackoffFactor(2);
        txEvent.setCurrentRetryTimes(0);
        txEvent.setMaxRetryTimes(2);
        txEvent.setNextRetryTime(LocalDateTime.of(2021, 1, 2, 0, 0, 0));
        txEvent.setEvent(new TestEvent());
        txEvent.setStatus(TransactionEvent.Status.PENDING);
        txEvent.setCreator("lokic");
        txEvent.setEditor("lokic");


        TransactionEvent<TestEvent> txEvent2 = new TransactionEvent<>();
        txEvent2.setInitBackoff(10L);
        txEvent2.setBackoffFactor(2);
        txEvent2.setCurrentRetryTimes(0);
        txEvent2.setMaxRetryTimes(2);
        txEvent2.setNextRetryTime(LocalDateTime.of(2021, 1, 2, 0, 0, 0));
        txEvent2.setEvent(new TestEvent());
        txEvent2.setStatus(TransactionEvent.Status.PENDING);
        txEvent2.setCreator("lokic");
        txEvent2.setEditor("lokic");

        repository.save(Lists.newArrayList(txEvent, txEvent2));

        List<TransactionEvent<? extends Event>> list = repository.queryEvents(
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                LocalDateTime.of(2021, 2, 1, 0, 0, 0),
                100);
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void test_updateSuccessByEventIds() {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .addScript("classpath:data.sql")
                .build();
        TransactionEventMysqlRepository repository = new TransactionEventMysqlRepository(new JdbcTemplateExtension(dataSource));
        repository.updateSuccessByEventIds("lokic", LocalDateTime.of(2021, 1, 2, 0, 0, 0), Lists.newArrayList(1L, 2L));

        List<TransactionEvent<? extends Event>> events = repository.queryEvents(
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                LocalDateTime.of(2022, 2, 1, 0, 0, 0),
                100);
        Assert.assertEquals(0, events.size());
    }

    @Test
    public void test_updateStatus() {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .addScript("classpath:data.sql")
                .build();
        TransactionEventMysqlRepository repository = new TransactionEventMysqlRepository(new JdbcTemplateExtension(dataSource));

        TransactionEvent<TestEvent> txEvent = new TransactionEvent<>();
        txEvent.setId(1L);
        txEvent.setInitBackoff(10L);
        txEvent.setBackoffFactor(2);
        txEvent.setCurrentRetryTimes(0);
        txEvent.setMaxRetryTimes(2);
        txEvent.setNextRetryTime(LocalDateTime.of(2021, 1, 2, 0, 0, 0));
        txEvent.setEvent(new TestEvent());
        txEvent.setStatus(TransactionEvent.Status.SUCCESS);
        txEvent.setCreator("lokic");
        txEvent.setEditor("lokic");

        repository.updateStatus(txEvent);

        List<TransactionEvent<? extends Event>> events = repository.queryEvents(
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                LocalDateTime.of(2022, 2, 1, 0, 0, 0),
                100);
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(2L, events.get(0).getId().longValue());

    }

    public static class TestEvent extends Event {

    }

}