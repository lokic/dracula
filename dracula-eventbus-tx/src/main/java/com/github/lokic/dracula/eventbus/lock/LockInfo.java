package com.github.lokic.dracula.eventbus.lock;

public class LockInfo {

    private final String lockKey;

    private final String lockValue;

    /**
     * 锁的有效期
     */
    private final long expireInMillis;

    public LockInfo(String lockKey, String lockValue, long expireInMillis) {
        this.lockKey = lockKey;
        this.lockValue = lockValue;
        this.expireInMillis = expireInMillis;
    }

    public String getLockKey() {
        return lockKey;
    }

    public String getLockValue() {
        return lockValue;
    }

    public long getExpireInMillis() {
        return expireInMillis;
    }
}
