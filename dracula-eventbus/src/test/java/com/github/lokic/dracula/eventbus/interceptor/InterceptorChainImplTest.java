package com.github.lokic.dracula.eventbus.interceptor;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.interceptor.extension.ExtensionInterceptor;
import com.github.lokic.dracula.eventbus.interceptor.internal.SpyInternalInterceptor;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InterceptorChainImplTest {


    @Test
    public void test_parseEnabledInterceptorAttributes() {
        @SuppressWarnings("unchecked")
        InterceptorChainImpl<TestEvent> interceptorChain = Mockito.mock(InterceptorChainImpl.class);
        Mockito.doCallRealMethod().when(interceptorChain).parseEnabledInterceptorAttributes(Mockito.anyList(), Mockito.anyList());

        InterceptorAttribute<TestEvent> interceptorAttribute1 = new InterceptorAttribute<>(buildInterceptor("A", 0));
        InterceptorAttribute<TestEvent> interceptorAttribute2 = new InterceptorAttribute<>(buildInterceptor("B", 1));
        InterceptorAttribute<TestEvent> interceptorAttribute3 = new InterceptorAttribute<>(buildInterceptor("C", 2));
        InterceptorAttribute<TestEvent> interceptorAttribute4 = new InterceptorAttribute<>(new SpyInternalInterceptor<>("D", 1));
        InterceptorAttribute<TestEvent> interceptorAttribute5 = new InterceptorAttribute<>(new SpyInternalInterceptor<>("E", 2));
        InterceptorAttribute<TestEvent> interceptorAttribute6 = new InterceptorAttribute<>(new SpyInternalInterceptor<>("F", 0));


        List<InterceptorAttribute<? extends TestEvent>> attributes = Stream.of(
                        interceptorAttribute1, interceptorAttribute2, interceptorAttribute3,
                        interceptorAttribute4, interceptorAttribute5)
                .collect(Collectors.toList());

        // 默认的启动和禁用
        Assert.assertEquals(
                Lists.newArrayList(interceptorAttribute1, interceptorAttribute3),
                interceptorChain.parseEnabledInterceptorAttributes(attributes,
                        Lists.newArrayList(Rule.of("A"), Rule.of("C")))
        );
        Assert.assertEquals(
                Lists.newArrayList(interceptorAttribute1, interceptorAttribute2),
                interceptorChain.parseEnabledInterceptorAttributes(attributes,
                        Lists.newArrayList(Rule.of("A"), Rule.of("C"), Rule.of("B"), Rule.of("-C")))
        );

        // default的位置
        Assert.assertEquals(
                Lists.newArrayList(interceptorAttribute4, interceptorAttribute5, interceptorAttribute1, interceptorAttribute3),
                interceptorChain.parseEnabledInterceptorAttributes(attributes,
                        Lists.newArrayList(Rule.of("default"), Rule.of("A"), Rule.of("C")))
        );
        Assert.assertEquals(
                Lists.newArrayList(interceptorAttribute1, interceptorAttribute4, interceptorAttribute5, interceptorAttribute3),
                interceptorChain.parseEnabledInterceptorAttributes(attributes,
                        Lists.newArrayList(Rule.of("A"), Rule.of("default"), Rule.of("C")))
        );

        // 禁用default内部的拦截器
        Assert.assertEquals(
                Lists.newArrayList(interceptorAttribute4, interceptorAttribute1, interceptorAttribute3),
                interceptorChain.parseEnabledInterceptorAttributes(attributes,
                        Lists.newArrayList(Rule.of("default"), Rule.of("A"), Rule.of("C"), Rule.of("-E")))
        );
        Assert.assertEquals(
                Lists.newArrayList(interceptorAttribute5, interceptorAttribute1, interceptorAttribute3),
                interceptorChain.parseEnabledInterceptorAttributes(attributes,
                        Lists.newArrayList(Rule.of("default"), Rule.of("A"), Rule.of("C"), Rule.of("-D")))
        );

        // default内部排序，order越小，顺序越靠前
        List<InterceptorAttribute<? extends TestEvent>> attributes2 = Stream.of(
                        interceptorAttribute1, interceptorAttribute2, interceptorAttribute3,
                        interceptorAttribute4, interceptorAttribute5, interceptorAttribute6)
                .collect(Collectors.toList());
        Assert.assertEquals(
                Lists.newArrayList(interceptorAttribute6, interceptorAttribute4, interceptorAttribute5, interceptorAttribute1, interceptorAttribute3),
                interceptorChain.parseEnabledInterceptorAttributes(attributes2, Lists.newArrayList(Rule.of("default"), Rule.of("A"), Rule.of("C")))
        );

    }

    public ExtensionInterceptor<TestEvent> buildInterceptor(String name, int order) {
        return new ExtensionInterceptor<TestEvent>() {
            @Override
            public int getOrder() {
                return order;
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

    public static class TestEvent extends Event {

    }

}