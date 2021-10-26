package com.github.lokic.dracula.eventbus.lock;

/**
 * 没有分布式锁
 */
public class NonDistributedLocker implements DistributedLocker {

    @Override
    public boolean hasLock() {
        return true;
    }

    @Override
    public boolean tryLock() {
        return true;
    }

    @Override
    public boolean unlock() {
        return true;
    }
}
