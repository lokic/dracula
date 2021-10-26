package com.github.lokic.dracula.eventbus.lock.db;

import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class LockRepositoryTest {


    @Test
    public void test_create() {
        LockRepository lockRepository = getLockRepo();

        Date expiredTime = new Date(System.currentTimeMillis() + 10000);
        LockPo lockPo = new LockPo();
        lockPo.setLockKey("test_key");
        lockPo.setLockValue("test_value");
        lockPo.setExpiredTime(expiredTime);
        int createCount = lockRepository.createLock(lockPo);
        assertEquals(1, createCount);

        LockPo dbLockPo = lockRepository.getLockPo("test_key");
        assertNotNull(dbLockPo);
        assertEquals("test_key", dbLockPo.getLockKey());
        assertEquals("test_value", dbLockPo.getLockValue());
        assertEquals(expiredTime, dbLockPo.getExpiredTime());
    }

    @Test
    public void test_delete() {
        LockRepository lockRepository = getLockRepo();
        LockPo deletePo = new LockPo();
        deletePo.setLockKey("pre_lock_key");
        deletePo.setLockValue("pre_lock_value");
        int deleteCount = lockRepository.deleteLock(deletePo);
        assertEquals(1, deleteCount);

        LockPo dbLockPo2 = lockRepository.getLockPo("pre_lock_key");
        assertNull(dbLockPo2);
    }

    @Test
    public void test_update() {
        LockRepository lockRepository = getLockRepo();
        LockPo dbLockPo = lockRepository.getLockPo("pre_lock_key");
        dbLockPo.setLockValue("pre_lock_value2");
        int updateCount = lockRepository.updateLock(dbLockPo);
        assertEquals(1, updateCount);

        LockPo dbLockPo2 = lockRepository.getLockPo("pre_lock_key");
        assertNotNull(dbLockPo2);
        assertEquals("pre_lock_key", dbLockPo2.getLockKey());
        assertEquals("pre_lock_value2", dbLockPo2.getLockValue());
        assertEquals("2021-01-01 00:00:00", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dbLockPo2.getExpiredTime()));
    }

    private LockRepository getLockRepo() {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:lock_schema.sql")
                .addScript("classpath:lock_data.sql")
                .build();
        return new LockRepository(new JdbcTemplate(dataSource));
    }
}