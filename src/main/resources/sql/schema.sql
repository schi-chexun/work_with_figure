-- 数据库初始化脚本
-- 数据库名：app
-- 指尖农场 - 番茄钟应用

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `app` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `app`;

-- ========================================
-- 1. 用户表
-- ========================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`                 BIGINT(20)   NOT NULL COMMENT '主键ID（雪花ID）',
    `username`           VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`           VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    `nickname`           VARCHAR(50)           DEFAULT NULL COMMENT '昵称',
    `phone`              VARCHAR(20)           DEFAULT NULL COMMENT '手机号',
    `email`              VARCHAR(100)          DEFAULT NULL COMMENT '邮箱',
    `avatar`             VARCHAR(255)          DEFAULT NULL COMMENT '头像URL',
    `current_session_id` BIGINT(20)            DEFAULT NULL COMMENT '当前进行中的会话ID（限制只能开一局）',
    `status`             TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    `create_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`          BIGINT(20)            DEFAULT NULL COMMENT '创建人',
    `update_by`          BIGINT(20)            DEFAULT NULL COMMENT '更新人',
    `deleted`            TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`),
    KEY `idx_email` (`email`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户表';

-- ========================================
-- 2. 主题表
-- ========================================
DROP TABLE IF EXISTS `theme`;
CREATE TABLE `theme`
(
    `id`          BIGINT(20)  NOT NULL COMMENT '主键ID',
    `name`        VARCHAR(50) NOT NULL COMMENT '主题名称',
    `code`        VARCHAR(30) NOT NULL COMMENT '主题代码',
    `description` VARCHAR(200)         DEFAULT NULL COMMENT '主题描述',
    `icon`        VARCHAR(100)         DEFAULT NULL COMMENT '主题图标',
    `effect_type` VARCHAR(50)          DEFAULT NULL COMMENT '特效类型',
    `is_default`  TINYINT(1)           DEFAULT 0 COMMENT '是否默认主题',
    `status`      TINYINT(1)           DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='主题表';

-- ========================================
-- 3. 用户设置表
-- ========================================
DROP TABLE IF EXISTS `user_settings`;
CREATE TABLE `user_settings`
(
    `id`                  BIGINT(20) NOT NULL COMMENT '主键ID',
    `user_id`             BIGINT(20) NOT NULL COMMENT '用户ID',
    `theme_id`            BIGINT(20)          DEFAULT 1 COMMENT '当前主题ID',

    -- 番茄钟设置
    `work_duration`       INT                 DEFAULT 25 COMMENT '工作时长(分钟)',
    `short_break`         INT                 DEFAULT 5 COMMENT '短休息时长(分钟)',
    `long_break`          INT                 DEFAULT 15 COMMENT '长休息时长(分钟)',
    `long_break_interval` INT                 DEFAULT 4 COMMENT '几个番茄后长休息',

    -- 提醒设置
    `sound_enabled`       TINYINT(1)          DEFAULT 1 COMMENT '声音特效（0-关，1-开）',
    `water_reminder`      TINYINT(1)          DEFAULT 1 COMMENT '喝水提醒（0-关，1-开）',
    `water_interval`      INT                 DEFAULT 30 COMMENT '喝水提醒间隔(分钟)',
    `overwork_reminder`   TINYINT(1)          DEFAULT 1 COMMENT '工作过久提醒（0-关，1-开）',
    `overwork_threshold`  INT                 DEFAULT 120 COMMENT '过久阈值(分钟)',

    `create_time`         DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户设置表';

-- ========================================
-- 4. 番茄钟会话表（每一局）
-- ========================================
DROP TABLE IF EXISTS `pomodoro_session`;
CREATE TABLE `pomodoro_session`
(
    `id`                  BIGINT(20) NOT NULL COMMENT '主键ID',
    `user_id`             BIGINT(20) NOT NULL COMMENT '用户ID',
    `theme_id`            BIGINT(20)          DEFAULT NULL COMMENT '使用的主题ID',

    `start_time`          DATETIME   NOT NULL COMMENT '开始时间',
    `end_time`            DATETIME            DEFAULT NULL COMMENT '结束时间',
    `status`              TINYINT(1)          DEFAULT 0 COMMENT '状态（0-进行中，1-已完成，2-已放弃）',

    `total_work_seconds`  INT                 DEFAULT 0 COMMENT '总工作秒数',
    `total_break_seconds` INT                 DEFAULT 0 COMMENT '总休息秒数',
    `pomodoro_count`      INT                 DEFAULT 0 COMMENT '完成的番茄数',

    `create_time`         DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_status` (`user_id`, `status`),
    KEY `idx_user_date` (`user_id`, `start_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='番茄钟会话表';

-- ========================================
-- 5. 工作日志表（每次专注/休息记录）
-- ========================================
DROP TABLE IF EXISTS `work_log`;
CREATE TABLE `work_log`
(
    `id`              BIGINT(20) NOT NULL COMMENT '主键ID',
    `user_id`         BIGINT(20) NOT NULL COMMENT '用户ID',
    `session_id`      BIGINT(20) NOT NULL COMMENT '所属会话ID',

    `type`            TINYINT(1) NOT NULL COMMENT '类型（0-工作，1-短休息，2-长休息）',
    `planned_seconds` INT        NOT NULL COMMENT '计划时长(秒)',
    `actual_seconds`  INT                 DEFAULT 0 COMMENT '实际时长(秒)',

    `start_time`      DATETIME   NOT NULL COMMENT '开始时间',
    `end_time`        DATETIME            DEFAULT NULL COMMENT '结束时间',
    `completed`       TINYINT(1)          DEFAULT 0 COMMENT '是否完成（0-否，1-是）',

    `create_time`     DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`         TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_date` (`user_id`, `start_time`),
    KEY `idx_session` (`session_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='工作日志表';

-- ========================================
-- 6. 每日统计表
-- ========================================
DROP TABLE IF EXISTS `daily_stats`;
CREATE TABLE `daily_stats`
(
    `id`                  BIGINT(20) NOT NULL COMMENT '主键ID',
    `user_id`             BIGINT(20) NOT NULL COMMENT '用户ID',
    `stat_date`           DATE       NOT NULL COMMENT '统计日期',

    `total_work_seconds`  INT                 DEFAULT 0 COMMENT '总工作秒数',
    `total_sessions`      INT                 DEFAULT 0 COMMENT '会话数',
    `completed_pomodoros` INT                 DEFAULT 0 COMMENT '完成的番茄数',

    `create_time`         DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_date` (`user_id`, `stat_date`),
    KEY `idx_date` (`stat_date`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='每日统计表';

-- ========================================
-- 初始化数据
-- ========================================

-- 默认主题
INSERT INTO `theme` (`id`, `name`, `code`, `description`, `effect_type`, `is_default`, `status`)
VALUES (1, '指尖农场', 'farm', '种田主题，按键显示植物生长特效，收获丰收的喜悦', 'plant', 1, 1),
       (2, '海边度假', 'beach', '海边主题，抛网捕鱼，收获海鲜大餐', 'fishing', 0, 1),
       (3, '星空冥想', 'starry', '星空主题，宁静的夜空陪伴你专注', 'star', 0, 1),
       (4, '森林漫步', 'forest', '森林主题，聆听自然的声音', 'leaf', 0, 1);

-- 测试用户（密码：123456）
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `phone`, `email`, `status`)
VALUES (1, 'admin', '$2a$10$XcigeMfToGQ2ogwdEXTFaO7o.2Y4T9K5l2YcVwV9J8I9J6c5zcYh6', '管理员', '13800138000',
        'admin@example.com', 1);

-- 管理员默认设置
INSERT INTO `user_settings` (`id`, `user_id`, `theme_id`, `work_duration`, `short_break`, `long_break`,
                             `long_break_interval`, `sound_enabled`, `water_reminder`, `water_interval`,
                             `overwork_reminder`, `overwork_threshold`)
VALUES (1, 1, 1, 25, 5, 15, 4, 1, 1, 30, 1, 120);
