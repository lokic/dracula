package com.github.lokic.dracula.eventbus.interceptor;


import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 拦截器的规则。
 * <p>
 * rule 前面是 {@code DISABLE_VALUE_PREFIX} 表示是禁用这个拦截器。一般用于对一组固定顺序的拦截器中，禁用个别拦截器，如，禁用内部拦截器
 * <p>
 * rule的名字 {@code name}，不能 {@code DISABLE_VALUE_PREFIX} 打头
 */

@EqualsAndHashCode
public class Rule {


    private static final Map<String, Rule> RULE_CACHE = new ConcurrentHashMap<>();

    public static final String DISABLE_VALUE_PREFIX = "-";

    /**
     * 由于不开放内部拦截器顺序的调整，所以内部拦截器的rule统一叫default
     */
    private static final String INTERNAL_DEFAULT_KEY = "default";

    private static final Rule DEFAULT_RULE = Rule.of(INTERNAL_DEFAULT_KEY);

    private final boolean enabled;

    private final String name;

    public static Rule defaultRule() {
        return DEFAULT_RULE;
    }

    public static Rule of(String rule) {
        return RULE_CACHE.computeIfAbsent(rule, Rule::create);
    }

    private static Rule create(String rule) {
        if (rule.startsWith(DISABLE_VALUE_PREFIX)) {
            return new Rule(false, rule.substring(DISABLE_VALUE_PREFIX.length()));
        } else {
            return new Rule(true, rule);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    private String buildRuleName(boolean enable, String name) {
        return enable ? name : DISABLE_VALUE_PREFIX + name;
    }

    private Rule(boolean enabled, String name) {
        if (name.startsWith(DISABLE_VALUE_PREFIX)) {
            throw new IllegalArgumentException("rule name [" + name + "] can not start with " + DISABLE_VALUE_PREFIX);
        }
        this.enabled = enabled;
        this.name = name;
    }

    @Override
    public String toString() {
        return buildRuleName(enabled, name);
    }
}
