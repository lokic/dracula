package com.github.lokic.dracula.eventbus.lock;

public interface DistributedLockerFactory {

    default DistributedLocker create(LockInfo info){
        return new DefaultDistributedLocker();
    }
}
