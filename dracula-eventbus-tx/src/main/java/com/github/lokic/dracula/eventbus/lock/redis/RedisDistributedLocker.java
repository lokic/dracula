package com.github.lokic.dracula.eventbus.lock.redis;

import com.github.lokic.dracula.eventbus.lock.DistributedLocker;

/**
 * todo 实现
 */
public class RedisDistributedLocker implements DistributedLocker {

    @Override
    public boolean hasLock() {
        return false;
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean unlock() {
        return false;
    }
}
