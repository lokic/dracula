package com.github.lokic.dracula.eventbus.lock.db;

import com.github.lokic.dracula.eventbus.lock.DistributedLocker;
import com.github.lokic.dracula.eventbus.lock.LockInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class DbDistributedLocker implements DistributedLocker {

    private final LockRepository lockRepository;
    private final LockInfo info;

    public DbDistributedLocker(LockRepository lockRepository, LockInfo info) {
        this.lockRepository = lockRepository;
        this.info = info;
    }

    @Override
    public boolean hasLock() {
        LockPo dbLock = lockRepository.getLockPo(info.getLockKey());
        return info.getLockValue().equals(dbLock.getLockValue())
                && !isExpired(dbLock, new Date());
    }

    @Override
    public boolean tryLock() {
        LockPo dbLock = lockRepository.getLockPo(info.getLockKey());
        Date now = new Date();
        if (dbLock == null) {
            Date expiredTime = new Date(now.getTime() + info.getExpireInMillis());
            LockPo newLockPo = new LockPo();
            newLockPo.setLockKey(info.getLockKey());
            newLockPo.setLockValue(info.getLockValue());
            newLockPo.setExpiredTime(expiredTime);
            int count = lockRepository.createLock(newLockPo);
            return count == 1;
        } else {
            if (isExpired(dbLock, now)) {
                Date expiredTime = new Date(now.getTime() + info.getExpireInMillis());
                LockPo newLockPo = new LockPo();
                newLockPo.setLockKey(info.getLockKey());
                newLockPo.setLockValue(info.getLockValue());
                newLockPo.setExpiredTime(expiredTime);
                newLockPo.setUpdatedTime(dbLock.getUpdatedTime());
                int count = lockRepository.updateLock(newLockPo);
                return count == 1;
            }
        }
        return false;
    }


    private boolean isExpired(LockPo dbLock, Date now) {
        return now.after(dbLock.getExpiredTime());
    }

    @Override
    public boolean unlock() {
        if (hasLock()) {
            LockPo deleteLockPo = new LockPo();
            deleteLockPo.setLockKey(info.getLockKey());
            deleteLockPo.setLockValue(info.getLockValue());
            int count = lockRepository.deleteLock(deleteLockPo);
            if (count == 1) {
                return true;
            } else {
                log.info("unlock update fail, lockKey = {}, lockValue = {}", info.getLockKey(), info.getLockValue());
            }
        } else {
            log.info("unlock not hasLock, lockKey = {}, lockValue = {}", info.getLockKey(), info.getLockValue());
        }
        return false;
    }

}
