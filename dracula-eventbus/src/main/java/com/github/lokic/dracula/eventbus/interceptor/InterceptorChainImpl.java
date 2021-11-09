package com.github.lokic.dracula.eventbus.interceptor;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.interceptor.internal.EventTypeInterceptor;
import com.github.lokic.javaplus.Collectors.Distinct;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.github.lokic.javaplus.Streams.Fors.For;
import static com.github.lokic.javaplus.functional.tuple.TupleFunctional.function;
import static com.github.lokic.javaplus.functional.tuple.TupleFunctional.predicate;
import static java.util.stream.Collectors.toList;

public class InterceptorChainImpl<E extends Event> implements InterceptorChain<E> {

    private final List<Interceptor<? extends E>> interceptors;

    public InterceptorChainImpl(Class<E> eventClazz, List<InterceptorAttribute<? extends E>> attributes, List<Rule> eventHandlerRules) {
        this.interceptors = initInterceptors(eventClazz, attributes, eventHandlerRules);
    }

    private List<Interceptor<? extends E>> initInterceptors(Class<E> eventClazz, List<InterceptorAttribute<? extends E>> attributes, List<Rule> eventHandlerRules) {
        List<InterceptorAttribute<? extends E>> iAttrs = buildInterceptorAttributes(eventClazz);
        iAttrs.addAll(attributes);

        // 相同name的rule只会保留最后一个
        List<Rule> distinctEventHandlerRules =
                Stream.concat(Stream.of(Rule.defaultRule()), eventHandlerRules.stream())
                        .collect(Distinct.distinctLastPutByKey(Rule::getName));

        List<InterceptorAttribute<? extends E>> enabledIAttrs = parseEnabledInterceptorAttributes(iAttrs, distinctEventHandlerRules);

        return enabledIAttrs.stream()
                .map(InterceptorAttribute::getInterceptor)
                .collect(toList());
    }

    @VisibleForTesting
    List<InterceptorAttribute<? extends E>> parseEnabledInterceptorAttributes(
            List<InterceptorAttribute<? extends E>> attributes, List<Rule> eventHandlerRules) {
        /*
         * 引入 {@link com.github.lokic.javaplus.Collectors.toMapTupleStream}
         * 和 {@link com.github.lokic.javaplus.functional.tuple.TupleFunctional}
         * 把二元组的二个参数解包出来并命名，用于提高代码可读性。
         */
        return attributes.stream()
                .flatMap(For((iAttr -> eventHandlerRules.stream())))
                .filter(predicate(this::isMatch))
                .collect(
                        com.github.lokic.javaplus.Collectors.toMapTupleStream(
                                (iAttr, eRule) -> iAttr,
                                (iAttr, eRule) -> eRule.isEnabled(),
                                (isEnable1, isEnable2) -> isEnable1 && isEnable2,
                                HashMap::new))
                .filter(predicate((iAttr, isEnabled) -> isEnabled))
                .map(function((iAttr, isEnabled) -> iAttr))
                .sorted(InterceptorComparator.of(eventHandlerRules))
                .collect(toList());
    }

    private boolean isMatch(InterceptorAttribute<?> attribute, Rule eRule) {
        // 由于default的拦截器组会对内部的拦截器单独禁用，所以default的拦截器组会有一个name的匹配
        if (Objects.equals(attribute.getRule(), Rule.defaultRule())) {
            if (Objects.equals(attribute.getName(), eRule.getName())) {
                return true;
            }
        }
        return Objects.equals(attribute.getRule().getName(), eRule.getName());
    }

    private List<InterceptorAttribute<? extends E>> buildInterceptorAttributes(Class<E> eventClazz) {
        // 强制第一个必须是事件类型的拦截器，用于把不支持的事件直接过滤掉
        return Lists.newArrayList(new InterceptorAttribute<>(new EventTypeInterceptor<>(eventClazz)));
    }

    @Override
    public List<Interceptor<? extends E>> getInterceptors() {
        return interceptors;
    }
}
