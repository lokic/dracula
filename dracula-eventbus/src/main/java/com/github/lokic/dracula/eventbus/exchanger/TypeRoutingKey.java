package com.github.lokic.dracula.eventbus.exchanger;

public class TypeRoutingKey implements RoutingKey {

    private final Class<?> routingKey;

    public TypeRoutingKey(Class<?> clazz) {
        this.routingKey = clazz;
    }

    @Override
    public boolean match(Class<?> clazz) {
        return routingKey.isAssignableFrom(clazz);
    }

}
