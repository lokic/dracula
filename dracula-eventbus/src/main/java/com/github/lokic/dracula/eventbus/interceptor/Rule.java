package com.github.lokic.dracula.eventbus.interceptor;


import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 拦截器的规则。
 * <p>
 * rule 前面是 {@code REMOVE_VALUE_PREFIX} 表示是移除这个拦截器。一般用于对一组固定顺序的拦截器中，移除个别拦截器，如，移除内部拦截器
 * <p>
 * rule的名字 {@code name}，不能 {@code REMOVE_VALUE_PREFIX} 打头
 */

@EqualsAndHashCode
public class Rule {


    private static final Map<String, Rule> RULE_CACHE = new ConcurrentHashMap<>();

    /**
     * 由于不开放内部拦截器顺序的调整，所以内部拦截器的rule统一叫default
     */
    private static final String INTERNAL_DEFAULT_KEY = "default";

    private static final String REMOVE_VALUE_PREFIX = "-";

    private static final Rule DEFAULT_RULE = Rule.of(INTERNAL_DEFAULT_KEY);

    private final boolean remove;

    private final String name;

    public static Rule of(String rule) {
        return RULE_CACHE.computeIfAbsent(rule, Rule::create);
    }

    public static Rule defaultRule() {
        return DEFAULT_RULE;
    }

    public Rule reverse() {
        return Rule.of(buildRuleName(!remove, name));
    }

    private boolean isRemove() {
        return remove;
    }

    private static Rule create(String rule) {
        if (rule.startsWith(REMOVE_VALUE_PREFIX)) {
            return new Rule(true, rule.substring(REMOVE_VALUE_PREFIX.length()));
        } else {
            return new Rule(false, rule);
        }
    }

    private String buildRuleName(boolean remove, String name) {
        return remove ? REMOVE_VALUE_PREFIX + name : name;
    }

    private Rule(boolean remove, String name) {
        if (name.startsWith(REMOVE_VALUE_PREFIX)) {
            throw new IllegalArgumentException("name can not start with " + REMOVE_VALUE_PREFIX + ": " + name);
        }
        this.remove = remove;
        this.name = name;
    }

    @Override
    public String toString() {
        return (isRemove() ? "-" : "") + name;
    }
}
