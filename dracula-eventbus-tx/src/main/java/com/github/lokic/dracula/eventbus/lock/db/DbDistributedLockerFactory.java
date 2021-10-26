package com.github.lokic.dracula.eventbus.lock.db;

import com.github.lokic.dracula.eventbus.lock.DistributedLocker;
import com.github.lokic.dracula.eventbus.lock.DistributedLockerFactory;
import com.github.lokic.dracula.eventbus.lock.LockInfo;

public class DbDistributedLockerFactory implements DistributedLockerFactory {

    private final LockRepository lockRepository;

    public DbDistributedLockerFactory(LockRepository lockRepository) {
        this.lockRepository = lockRepository;
    }

    @Override
    public DistributedLocker create(LockInfo info) {
        return new DbDistributedLocker(lockRepository, info);
    }

}
