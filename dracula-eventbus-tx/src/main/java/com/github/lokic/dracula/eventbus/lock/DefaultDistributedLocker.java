package com.github.lokic.dracula.eventbus.lock;

public class DefaultDistributedLocker implements DistributedLocker {

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
