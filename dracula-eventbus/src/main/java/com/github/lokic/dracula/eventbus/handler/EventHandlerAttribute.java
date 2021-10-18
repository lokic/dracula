package com.github.lokic.dracula.eventbus.handler;

import com.github.lokic.dracula.eventbus.executor.EventExecutor;
import com.github.lokic.dracula.eventbus.interceptor.Rule;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * 事件处理器相关属性的封装。
 */
@Getter
public class EventHandlerAttribute {

    private final EventExecutor executor;

    private final List<Rule> rules;

    public static EventHandlerAttribute sync() {
        return new EventHandlerAttribute(EventExecutor.SYNC, new ArrayList<>());
    }

    public EventHandlerAttribute(EventExecutor executor, List<String> rules) {

        Objects.requireNonNull(rules);
        validateRulesDuplicateProtection(rules);

        this.executor = executor;
        this.rules = rules.stream()
                .map(Rule::of)
                .collect(toList());
    }

    /**
     * 校验 {@code rules} 重复配置，不能有重复的名字
     *
     * @param rules
     */
    void validateRulesDuplicateProtection(List<String> rules) {
        Map<String, Long> map = rules.stream()
                .collect(Collectors.groupingBy(Function.identity(), counting()))
                .entrySet()
                .stream()
                .filter(e -> e.getValue() > 1)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (map.size() > 0) {
            String names = String.join(", ", map.keySet());
            throw new IllegalArgumentException("rules duplicate protection " + names);
        }
    }

}
