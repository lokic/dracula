package com.github.lokic.dracula.eventbus.interceptors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class InterceptorComparator implements Comparator<InterceptorAttribute<?>> {

    /**
     * 默认 内置拦截器 优先于 扩展拦截器
     */
    private static final Comparator<InterceptorAttribute<?>> COMPARATOR = Comparator
            .<InterceptorAttribute<?>, InterceptorType>comparing(InterceptorAttribute::getType)
            .thenComparingInt(InterceptorAttribute::getOrder);


    private final RuleOrdering ruleOrdering;

    static InterceptorComparator of(List<Rule> rules) {
        return new InterceptorComparator(rules);
    }

    private InterceptorComparator(List<Rule> rules) {
        this.ruleOrdering = new RuleOrdering(rules);
    }

    /**
     * 在 {@link RuleOrdering ruleOrdering} 里配置的情况下，顺序以 {@code ruleOrdering} 为准；
     * 没有{@code ruleOrdering} 里配置的情况，默认 内置拦截器 优先于 扩展拦截器，并以 {@link InterceptorAttribute#getOrder()} 排序，越小的优先级越高
     *
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(InterceptorAttribute o1, InterceptorAttribute o2) {

        if (Objects.equals(o1, o2)) {
            return 0;
        }

        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }

        if (ruleOrdering.contains(o1) && ruleOrdering.contains(o2)) {
            if (!Objects.equals(o1.getRule(), o2.getRule())) {
                return ruleOrdering.compare(o1, o2);
            }
        }
        return COMPARATOR.compare(o1, o2);
    }

    public static class RuleOrdering {

        /**
         * rules的排序
         */
        private final Ordering<Rule> ordering;

        private final Set<Rule> rules;

        RuleOrdering(List<Rule> rules) {
            this.rules = ImmutableSet.copyOf(rules);
            this.ordering = Ordering.explicit(rules);
        }

        boolean contains(InterceptorAttribute<?> o) {
            return rules.contains(o.getRule());
        }

        int compare(InterceptorAttribute<?> o1, InterceptorAttribute<?> o2) {
            return ordering.compare(o1.getRule(), o2.getRule());
        }
    }

}
