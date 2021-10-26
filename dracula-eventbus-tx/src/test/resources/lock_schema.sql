DROP TABLE IF EXISTS `dr_distributed_lock`;

CREATE TABLE `dr_distributed_lock`
(
    id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    lock_key     VARCHAR(255) COMMENT '锁的key',
    lock_value   VARCHAR(255) COMMENT '锁的value',
    expired_time TIMESTAMP(3) NOT NULL COMMENT '锁的过期事件',
    created_time TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_time TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP (3),
    UNIQUE KEY uk_lock_key (lock_key)
);