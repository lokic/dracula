package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.event.IntegrationEvent;
import com.github.lokic.dracula.eventbus.broker.Publisher;
import com.github.lokic.dracula.eventbus.executors.AsyncEventExecutor;
import com.github.lokic.dracula.eventbus.executors.SyncEventExecutor;
import com.github.lokic.dracula.eventbus.executors.threadpool.GracefulSpringThreadPool;
import com.github.lokic.dracula.eventbus.handlers.EventHandler;
import com.github.lokic.dracula.eventbus.handlers.HandlerContext;
import com.github.lokic.dracula.eventbus.interceptors.extensions.ExtensionInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.Executor;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@Import(EnableEventBusTest.TestConfig.class)
public class EnableEventBusTest {


    @Autowired
    private ApplicationContext context;

    @SuppressWarnings("unchecked")
    @Test
    public void eventHandler_test() {

        SoftAssertions.assertSoftly(softly -> {

            // spring register
            softly.assertThat(context.getBeansOfType(EventBus.class)).hasSize(1);
            softly.assertThat(context.getBeansOfType(EventHandler.class)).hasSize(2);
            softly.assertThat(context.getBeansOfType(SyncEventExecutor.class)).hasSize(1);
            softly.assertThat(context.getBeansOfType(AsyncEventExecutor.class)).hasSize(1);
            softly.assertThat(context.getBeansOfType(TestIntegrationEventPublisher.class)).hasSize(1);


            // inject
            softly.assertThat(context.getBean(TestIntegrationEventPublisher.class).testService).isNotNull();
            softly.assertThat(context.getBean(Spy1EventHandler.class).testService).isNotNull();

        });
    }


    @PublisherComponent
    public static class TestIntegrationEventPublisher implements Publisher<TestIntegrationEvent> {

        @Autowired
        private TestService testService;

        @Override
        public void publish(TestIntegrationEvent event) {
            testService.method();
        }

        @Override
        public Class<TestIntegrationEvent> getGenericType() {
            return TestIntegrationEvent.class;
        }
    }


    @Slf4j
    @EventHandlerComponent(executor = AsyncEventExecutor.class)
    public static class Spy2EventHandler implements EventHandler<TestEvent> {

        @Override
        public void handle(TestEvent event) {
            log.info("event => {}", event);
        }

    }

    @Slf4j
    @EventHandlerComponent
    public static class Spy1EventHandler implements EventHandler<TestEvent> {

        private TestService testService;

        public Spy1EventHandler(TestService testService) {
            this.testService = testService;
        }

        @Override
        public void handle(TestEvent event) {
            log.info("event => {}", event);
        }
    }

    public static class SpyInterceptor implements ExtensionInterceptor<TestEvent> {
        @Override
        public boolean onStart(Event event, HandlerContext context) {
            return false;
        }

        @Override
        public void onSuccess(Event event, HandlerContext context) {

        }

        @Override
        public void onException(Event event, HandlerContext context, Throwable exception) {

        }

        @Override
        public void onFinal(Event event, HandlerContext context) {

        }

        @Override
        public String getName() {
            return "testInterceptor";
        }

        @Override
        public int getOrder() {
            return 0;
        }
    }

    @EnableEventBus(eventBus = DefaultEventBus.class)
    @Configuration
    public static class TestConfig {

        @Bean
        public Executor threadPoolTaskSchedulerEventExecutor() {
            return new GracefulSpringThreadPool("test", 10);
        }

        @Bean
        public SpyInterceptor testInterceptor() {
            return new SpyInterceptor();
        }


        @Bean
        public Executor asyncExecutor() {
            return new GracefulSpringThreadPool("test", 10);
        }

        @Bean
        public AsyncEventExecutor defaultAsyncEventExecutor() {
            return new AsyncEventExecutor(threadPoolTaskSchedulerEventExecutor());
        }

        @Bean
        public TestService testService() {
            return new TestService();
        }
    }

    public static class TestEvent extends Event {

    }


    public static class TestIntegrationEvent extends IntegrationEvent {

    }


    @Service
    public static class TestService {

        public void method() {

        }

    }

}
