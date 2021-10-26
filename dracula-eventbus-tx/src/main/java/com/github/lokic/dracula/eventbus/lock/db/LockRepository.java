package com.github.lokic.dracula.eventbus.lock.db;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.util.Date;

public class LockRepository {

    private final JdbcTemplate jdbcTemplate;

    public LockRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public LockPo getLockPo(String lockKey) {
        return jdbcTemplate.query("" +
                        "SELECT lock_key, lock_value, expired_time, updated_time " +
                        "FROM dr_distributed_lock " +
                        "WHERE lock_key = ?",
                ps -> {
                    ps.setString(1, lockKey);
                },
                (rs) -> {
                    if (rs.next()) {
                        LockPo po = new LockPo();
                        po.setLockKey(rs.getString("lock_key"));
                        po.setLockValue(rs.getString("lock_value"));
                        po.setExpiredTime(new Date(rs.getTimestamp("expired_time").getTime()));
                        po.setUpdatedTime(new Date(rs.getTimestamp("updated_time").getTime()));
                        return po;
                    } else {
                        return null;
                    }
                });
    }

    public int createLock(LockPo lockPo) {
        return jdbcTemplate.update("" +
                        "INSERT INTO dr_distributed_lock(lock_key, lock_value, expired_time) " +
                        "VALUES(?, ?, ?)",
                lockPo.getLockKey(),
                lockPo.getLockValue(),
                new Timestamp(lockPo.getExpiredTime().getTime()));
    }

    public int updateLock(LockPo lockPo) {
        return jdbcTemplate.update("" +
                        "UPDATE dr_distributed_lock " +
                        "SET lock_value = ?, expired_time = ? " +
                        "WHERE lock_key = ? AND updated_time = ?",
                lockPo.getLockValue(), new Timestamp(lockPo.getExpiredTime().getTime()),
                lockPo.getLockKey(), new Timestamp(lockPo.getUpdatedTime().getTime()));
    }

    public int deleteLock(LockPo lockPo) {
        return jdbcTemplate.update("" +
                        "DELETE FROM dr_distributed_lock " +
                        "WHERE lock_key = ? AND lock_value = ?",
                lockPo.getLockKey(), lockPo.getLockValue());
    }
}
