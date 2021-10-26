package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.eventbus.config.TransactionalEventBusAutoConfiguration;
import com.github.lokic.dracula.eventbus.exchanger.Exchanger;
import com.github.lokic.dracula.eventbus.executor.AsyncEventExecutor;
import com.github.lokic.dracula.eventbus.executor.SyncEventExecutor;
import com.github.lokic.dracula.eventbus.handler.EventHandler;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PropertySource(value = "classpath:application.properties")
@Import({TransactionalEventBusAutoConfiguration.class, TestConfig.class})
public class EnableEventBusTest {


    @Autowired
    private ApplicationContext context;

    @Test
    public void eventHandler_test() {

        SoftAssertions.assertSoftly(softly -> {

            // spring register
            softly.assertThat(context.getBeansOfType(EventBus.class)).hasSize(1);
            softly.assertThat(context.getBeansOfType(Exchanger.class)).hasSize(1);
            softly.assertThat(context.getBeansOfType(QueueBeanPostProcessor.class)).hasSize(1);
            softly.assertThat(context.getBeansOfType(EventHandlerBeanPostProcessor.class)).hasSize(1);


            softly.assertThat(context.getBeansOfType(EventHandler.class)).hasSize(2);
            softly.assertThat(context.getBeansOfType(SyncEventExecutor.class)).hasSize(1);
            softly.assertThat(context.getBeansOfType(AsyncEventExecutor.class)).hasSize(1);
            softly.assertThat(context.getBeansOfType(TestIntegrationEventPublisher.class)).hasSize(1);
            softly.assertThat(context.getBeansOfType(TestIntegrationEventTxPublisher.class)).hasSize(1);


            // inject
            softly.assertThat(context.getBean(Spy1EventHandler.class).testService).isNotNull();
            softly.assertThat(context.getBean(Spy2EventHandler.class).testService).isNotNull();

        });
    }


    @Service
    public static class TestService {

        public void method() {

        }

    }

}
