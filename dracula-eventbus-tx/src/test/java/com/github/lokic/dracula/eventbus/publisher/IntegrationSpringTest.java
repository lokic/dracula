package com.github.lokic.dracula.eventbus.publisher;

import com.github.lokic.dracula.eventbus.config.TransactionEventBusAutoConfiguration;
import com.github.lokic.dracula.eventbus.exchanger.Exchanger;
import com.github.lokic.dracula.eventbus.lock.DistributedLockerFactory;
import com.github.lokic.dracula.eventbus.transaction.TransactionEventManager;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PropertySource(value = "classpath:application.properties")
@Import({IntegrationSpringTest.TestConfig.class, TransactionEventBusAutoConfiguration.class})
public class IntegrationSpringTest {


    @Autowired
    private ApplicationContext context;

    @SpyBean
    private TransactionEventManager transactionEventManager;

    @Test
    public void test() {

        SoftAssertions.assertSoftly(softly -> {

            // spring register
            softly.assertThat(context.getBeansOfType(TransactionEventManager.class)).hasSize(1);

        });

        Mockito.verify(transactionEventManager, Mockito.times(1)).init();
    }


    @Configuration
    public static class TestConfig {

        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                    .addScript("classpath:schema.sql")
                    .build();
        }

        @Bean
        public Exchanger exchanger() {
            return new Exchanger();
        }

        @Bean
        public DistributedLockerFactory distributedLockerFactory() {
            return new DistributedLockerFactory() {

            };
        }

    }
}
