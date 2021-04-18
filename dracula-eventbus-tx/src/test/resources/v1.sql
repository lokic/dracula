CREATE TABLE `dr_transactional_event`
(
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    current_retry_times TINYINT     NOT NULL DEFAULT 0 COMMENT '当前重试次数',
    max_retry_times     TINYINT     NOT NULL DEFAULT 5 COMMENT '最大重试次数',
    next_retry_time     DATETIME    NOT NULL COMMENT '下一次调度时间',

    event_key           VARCHAR(255) COMMENT '路由键',
    event_content       TEXT COMMENT '消息内容',
    status              TINYINT     NOT NULL DEFAULT 0 COMMENT '消息状态',

    init_backoff        BIGINT UNSIGNED NOT NULL DEFAULT 10 COMMENT '退避初始化值,单位为秒',
    backoff_factor      TINYINT     NOT NULL DEFAULT 2 COMMENT '退避因子(也就是指数)',
    creator             VARCHAR(20) NOT NULL DEFAULT 'admin',
    editor              VARCHAR(20) NOT NULL DEFAULT 'admin',
    deleted             TINYINT     NOT NULL DEFAULT 0,
    created_time        TIMESTAMP NULL,
    updated_time        TIMESTAMP NULL
);

INSERT INTO dr_transactional_event (event_key, event_content, status, current_retry_times, max_retry_times,
                                    next_retry_time, init_backoff, backoff_factor, creator, editor, created_time,
                                    updated_time)
VALUES ('1', '{}', 0, 0, 1, '2021-01-01 00:00:00', 10, 2, 'lokic', 'lokic', now(), now());

INSERT INTO dr_transactional_event (event_key, event_content, status, current_retry_times, max_retry_times,
                                    next_retry_time, init_backoff, backoff_factor, creator, editor, created_time,
                                    updated_time)
VALUES ('1', '{}', 0, 0, 1, '2021-01-01 00:00:00', 10, 2, 'lokic', 'lokic', now(), now());