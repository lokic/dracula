package com.github.lokic.dracula.eventbus.transaction.mysql;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.transaction.TransactionalEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionalEventMysqlRepositoryTest {

    private DataSource dataSource;

    private TransactionalEventMysqlRepository repository;

    @Before
    public void setup() {
        dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:v1.sql")
                .build();
        repository = new TransactionalEventMysqlRepository(new JdbcTemplateExtension(dataSource));
    }

    @Test
    public void test_queryEvents() {
        List<TransactionalEvent<? extends Event>> list = repository.queryEvents(
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                LocalDateTime.of(2021, 2, 1, 0, 0, 0),
                100);
        Assert.assertEquals(2, list.size());
    }

}