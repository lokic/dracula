package com.github.lokic.dracula.eventbus.lock.redis;

import com.github.lokic.dracula.eventbus.lock.DistributedLockerFactory;
import com.github.lokic.dracula.eventbus.lock.LockInfo;

public class RedisDistributedLockerFactory implements DistributedLockerFactory {

    @Override
    public RedisDistributedLocker create(LockInfo info) {
        return null;
    }
}
