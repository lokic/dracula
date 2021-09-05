package com.github.lokic.dracula.eventbus.lock;

public class LockInfo {

    private String lockKey;

    private String lockValue;

    /**
     * 锁的有效期
     */
    private long expireInMillis;

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
