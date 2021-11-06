package com.github.lokic.dracula.eventbus.interceptor;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.interceptor.internal.EventTypeInterceptor;
import com.github.lokic.javaplus.Collectors.Distinct;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.lokic.javaplus.Streams.Fors.For;
import static com.github.lokic.javaplus.functional.tuple.TupleFunctional.function;
import static com.github.lokic.javaplus.functional.tuple.TupleFunctional.predicate;
import static java.util.stream.Collectors.toList;


public class InterceptorChainImpl<E extends Event> implements InterceptorChain<E> {

    private final List<Interceptor<E>> interceptors;

    public InterceptorChainImpl(Class<E> eventClazz, List<InterceptorAttribute<E>> attributes, List<Rule> eventHandlerRules) {
        // 强制第一个必须是事件类型的拦截器
        List<InterceptorAttribute<E>> iAttrs =
                Lists.newArrayList(new InterceptorAttribute<>(new EventTypeInterceptor<>(eventClazz)));

        iAttrs.addAll(attributes);

        List<Rule> distinctEventHandlerRules = eventHandlerRules
                .stream()
                .collect(Distinct.distinctLastPut());

        List<InterceptorAttribute<E>> enabledIAttrs = iAttrs.stream()
                .flatMap(For((iAttr -> distinctEventHandlerRules.stream())))
                .filter(predicate((iAttr, eRule) -> iAttr.getRule().isTheRuleEnabled(eRule)))
                .map(function((iAttr, eRule) -> iAttr))
                .distinct()
                .sorted(InterceptorComparator.of(distinctEventHandlerRules))
                .collect(Collectors.toList());

        this.interceptors = enabledIAttrs.stream()
                .map(InterceptorAttribute::getInterceptor)
                .collect(toList());
    }

    @Override
    public List<Interceptor<E>> getInterceptors() {
        return interceptors;
    }
}
