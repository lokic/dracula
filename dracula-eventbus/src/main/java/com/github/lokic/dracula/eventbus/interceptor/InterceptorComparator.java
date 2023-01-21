package com.github.lokic.dracula.eventbus.interceptor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class InterceptorComparator {

    /**
     * 在 {@link RuleOrdering ruleOrdering} 里配置的情况下，顺序以 {@code ruleOrdering} 为准；
     * 没有{@code ruleOrdering} 里配置的情况，默认 内置拦截器 优先于 扩展拦截器，并以 {@link InterceptorAttribute#getOrder()} 排序，越小的优先级越高
     */
    static Comparator<InterceptorAttribute<?>> of(List<Rule> rules) {
        return Comparator.nullsFirst(new RuleOrdering(rules))
                .thenComparing(Comparator.comparing(InterceptorAttribute::getType))
                .thenComparingInt(InterceptorAttribute::getOrder);
    }

    static class RuleOrdering implements Comparator<InterceptorAttribute<?>> {

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

        @Override
        public int compare(InterceptorAttribute<?> o1, InterceptorAttribute<?> o2) {
            if (contains(o1) && contains(o2)) {
                return ordering.compare(o1.getRule(), o2.getRule());
            }
            return 0;
        }
    }

}
