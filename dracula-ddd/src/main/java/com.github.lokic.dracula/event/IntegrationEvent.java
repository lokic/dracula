package com.github.lokic.dracula.event;

/**
 * 同一个领域事件在应用层中可以有多个处理程序，一个处理程序可以解决聚合之间的一致性，另一个处理程序可以发布集成事件，以便其他微服务可以对它执行操作。
 */
public class IntegrationEvent extends Event {

}
