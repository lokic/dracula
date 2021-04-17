package com.github.lokic.dracula.eventbus.interceptors;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.javaplus.Collectors.Distinct;

import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;


public class InterceptorChainImpl<E extends Event> implements InterceptorChain<E> {

    private final Class<E> eventClazz;

    /**
     * 会引入所有没有被 {@link #filterAttrsWithRules} 过滤掉的拦截器
     * @param <E>
     */
    private final List<Interceptor<E>> interceptors;

    public InterceptorChainImpl(Class<E> eventClazz, List<InterceptorAttribute<E>> attributes, List<Rule> rules) {

        List<Rule> ruleList = rules
                .stream()
                .collect(Distinct.distinctLastPut());


        List<InterceptorAttribute<E>> sortedAttrs = attributes
                .stream()
                .filter(filterAttrsWithRules(ruleList))
                .sorted(InterceptorComparator.of(ruleList))
                .collect(toList());

        this.eventClazz = eventClazz;
        this.interceptors = sortedAttrs.stream()
                .map(InterceptorAttribute::getInterceptor)
                .collect(toList());
    }

    /**
     * 过滤掉配置了 {@code Rule#isRemove} 是 true 的 {@code InterceptorAttribute}。
     *
     * @param rules
     * @return
     */
    public Predicate<InterceptorAttribute<E>> filterAttrsWithRules(final List<Rule> rules) {
        return attr -> !rules.contains(attr.getRule().reverse());
    }

    @Override
    public List<Interceptor<E>> getInterceptors() {
        return interceptors;
    }
}
