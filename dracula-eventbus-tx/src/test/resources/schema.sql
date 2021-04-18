DROP TABLE IF EXISTS `dr_transactional_event`;

CREATE TABLE `dr_transactional_event`
(
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    current_retry_times TINYINT      NOT NULL DEFAULT 0 COMMENT '当前重试次数',
    max_retry_times     TINYINT      NOT NULL DEFAULT 5 COMMENT '最大重试次数',
    next_retry_time     DATETIME     NOT NULL COMMENT '下一次调度时间',

    event_key           VARCHAR(255) COMMENT '路由键',
    event_content       TEXT COMMENT '消息内容',
    status              TINYINT     NOT NULL DEFAULT 0 COMMENT '消息状态',

    init_backoff        BIGINT UNSIGNED NOT NULL DEFAULT 10 COMMENT '退避初始化值,单位为秒',
    backoff_factor      TINYINT     NOT NULL DEFAULT 2 COMMENT '退避因子(也就是指数)',
    creator             VARCHAR(20) NOT NULL DEFAULT 'admin',
    editor              VARCHAR(20) NOT NULL DEFAULT 'admin',
    deleted             TINYINT     NOT NULL DEFAULT 0,
    created_time        TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_time        TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP (3)
);
;