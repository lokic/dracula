package com.github.lokic.dracula.eventbus.lock.db;

import lombok.Data;

import java.util.Date;

@Data
class LockPo {
    private String lockKey;
    private String lockValue;
    private Date expiredTime;
    private Date updatedTime;
}
