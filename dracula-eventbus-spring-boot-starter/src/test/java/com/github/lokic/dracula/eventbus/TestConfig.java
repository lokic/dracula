package com.github.lokic.dracula.eventbus;

import com.github.lokic.dracula.eventbus.annotation.EnableEventBus;
import com.github.lokic.dracula.eventbus.executor.AsyncEventExecutor;
import com.github.lokic.dracula.eventbus.executor.threadpool.GracefulSpringThreadPool;
import com.github.lokic.dracula.eventbus.lock.DistributedLockerFactory;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.concurrent.Executor;

@EnableEventBus
@Configuration
public class TestConfig {

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
    public EnableEventBusTest.TestService testService() {
        return new EnableEventBusTest.TestService();
    }

    @Bean
    public DataSource dataSource() {
        return Mockito.mock(DataSource.class);
    }

    @Bean
    public DistributedLockerFactory distributedLockerFactory() {
        return new DistributedLockerFactory() {
        };
    }

}
