package com.github.lokic.dracula.eventbus.lock;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁对象
 */
public interface DistributedLocker extends Closeable {


    /**
     * 是否获取了锁
     * @return
     *  true获取了锁
     *  false没有获取锁
     */
    boolean hasLock();

    /**
     * 尝试获取锁，快速返回
     * @return
     */
    boolean tryLock();


    /**
     * 解锁
     * @return
     */
    boolean unlock();


    @Override
    default void close() {
        unlock();
    }

}
