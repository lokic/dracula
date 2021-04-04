package com.github.lokic.dracula.eventbus.handlers;

/**
 * 用于保存事件处理器处理时的一些上下文信息，如处理耗时之类。
 */
public interface HandlerContext {

    /**
     * 设置属性
     *
     * @param name  属性名
     * @param value 属性值
     */
    void setAttribute(String name, Object value);

    /**
     * 获取属性
     *
     * @param name 属性名
     * @return 对应名的属性值
     */
    Object getAttribute(String name);

    /**
     * 删除属性
     *
     * @param name 属性名
     * @return 被删除的属性值
     */
    Object removeAttribute(String name);

    /**
     * 判定是否存在指定属性
     *
     * @param name
     * @return
     */
    boolean hasAttribute(String name);

    /**
     * 获取所有属性key
     *
     * @return 属性key数组
     */
    String[] attributeNames();

}
