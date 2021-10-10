package com.github.lokic.dracula.eventbus.broker.exchanger;

interface RoutingKey {

    boolean match(Class<?> clazz);

}
