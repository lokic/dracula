package com.github.lokic.dracula.eventbus.interceptor;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.interceptor.extension.SpyExtensionInterceptor;
import com.github.lokic.dracula.eventbus.interceptor.internal.SpyInternalInterceptor;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InterceptorChainImplTest {

    private InterceptorChainImpl<TestEvent> interceptorChain;

    private List<InterceptorAttribute<? extends TestEvent>> attributes;

    private InterceptorAttribute<TestEvent> interceptorAttribute1;
    private InterceptorAttribute<TestEvent> interceptorAttribute2;
    private InterceptorAttribute<TestEvent> interceptorAttribute3;
    private InterceptorAttribute<TestEvent> interceptorAttribute4;
    private InterceptorAttribute<TestEvent> interceptorAttribute5;
    private InterceptorAttribute<TestEvent> interceptorAttribute6;

    @Before
    public void init() {
        interceptorChain = Mockito.mock(InterceptorChainImpl.class);
        Mockito.doCallRealMethod().when(interceptorChain).parseEnabledInterceptorAttributes(Mockito.anyList(), Mockito.anyList());

        interceptorAttribute1 = new InterceptorAttribute<>(new SpyExtensionInterceptor<>("A", 0));
        interceptorAttribute2 = new InterceptorAttribute<>(new SpyExtensionInterceptor<>("B", 1));
        interceptorAttribute3 = new InterceptorAttribute<>(new SpyExtensionInterceptor<>("C", 2));
        interceptorAttribute4 = new InterceptorAttribute<>(new SpyInternalInterceptor<>("D", 1));
        interceptorAttribute5 = new InterceptorAttribute<>(new SpyInternalInterceptor<>("E", 2));
        interceptorAttribute6 = new InterceptorAttribute<>(new SpyInternalInterceptor<>("F", 0));

        attributes = Stream.of(
                        interceptorAttribute1, interceptorAttribute2, interceptorAttribute3,
                        interceptorAttribute4, interceptorAttribute5)
                .collect(Collectors.toList());
    }


    @Test
    public void test_parseEnabledInterceptorAttributes() {
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


    }

    @Test
    public void test_parseEnabledInterceptorAttributes_defaultOrder() {
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
    }

    @Test
    public void test_parseEnabledInterceptorAttributes_disableInterceptorInDefault() {
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
    }

    @Test
    public void test_parseEnabledInterceptorAttributes_orderInDefault() {
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


    public static class TestEvent extends Event {

    }

}