package com.github.lokic.dracula.eventbus.interceptor;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.interceptor.extension.StubExtensionInterceptor;
import com.github.lokic.dracula.eventbus.interceptor.internal.StubInternalInterceptor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(DataProviderRunner.class)
public class InterceptorComparatorTest {


    @Test
    @UseDataProvider("getData")
    public void sort_test(String desc, List<InterceptorAttribute<?>> attributes, List<Rule> rules, List<String> expected) {

        List<String> sortedNames = attributes
                .stream()
                .sorted(InterceptorComparator.of(rules))
                .map(InterceptorAttribute::getName)
                .collect(Collectors.toList());

        Assert.assertEquals(expected, sortedNames);


    }


    @DataProvider
    public static Object[][] getData() {
        return new Object[][]{
                {"默认", ImmutableList.of(
                        mockExt("C", 0),
                        mockExt("A", 1),
                        mockExt("B", 2),
                        mockInt("D", 2)),
                        Lists.newArrayList(),
                        Lists.newArrayList("D", "C", "A", "B")},
                {"调整ext", ImmutableList.of(
                        mockExt("C", 0),
                        mockExt("A", 1),
                        mockExt("B", 2),
                        mockInt("D", 2)),
                        Lists.newArrayList(
                                Rule.of("A"),
                                Rule.of("B"),
                                Rule.of("C")),
                        Lists.newArrayList("D", "A", "B", "C")},
                {"调整default", ImmutableList.of(
                        mockExt("C", 0),
                        mockExt("A", 1),
                        mockExt("B", 2),
                        mockInt("D", 2)),
                        Lists.newArrayList(
                                Rule.of("A"),
                                Rule.of("default"),
                                Rule.of("C")),
                        Lists.newArrayList("A", "D", "C", "B")},
                {"调整internal name，不生效，默认第一个有default", ImmutableList.of(
                        mockExt("C", 0),
                        mockExt("A", 1),
                        mockExt("B", 2),
                        mockInt("D", 2)),
                        Lists.newArrayList(
                                Rule.of("A"),
                                Rule.of("B"),
                                Rule.of("C"),
                                Rule.of("D")),
                        Lists.newArrayList("D", "A", "B", "C")},
                {"调整default顺序", ImmutableList.of(
                        mockInt("C", 1),
                        mockInt("A", 0),
                        mockInt("B", 3),
                        mockInt("D", 2)),
                        Lists.newArrayList(
                                Rule.of("A"),
                                Rule.of("B"),
                                Rule.of("C"),
                                Rule.of("D")),
                        Lists.newArrayList("A", "C", "D", "B")}

        };
    }

    private static <E extends Event> InterceptorAttribute<E> mockInt(String name, int order) {
        return new InterceptorAttribute<>(new StubInternalInterceptor<>(name, order));
    }

    private static <E extends Event> InterceptorAttribute<E> mockExt(String name, int order) {
        return new InterceptorAttribute<>(new StubExtensionInterceptor<>(name, order));
    }

}