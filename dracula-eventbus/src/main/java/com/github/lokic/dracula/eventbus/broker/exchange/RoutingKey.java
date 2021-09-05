package com.github.lokic.dracula.eventbus.broker.exchange;

interface RoutingKey {

    boolean match(Class<?> clazz);

}
