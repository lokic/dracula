package com.github.lokic.dracula.eventbus.exchanger;

interface RoutingKey {

    boolean match(Class<?> clazz);

}
