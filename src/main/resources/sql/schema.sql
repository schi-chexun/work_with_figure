-- 数据库初始化脚本
-- 数据库名：app

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `app` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `app`;

-- 用户表（示例）
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`          BIGINT(20) NOT NULL COMMENT '主键ID（雪花ID）',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码',
    `nickname`    VARCHAR(50)           DEFAULT NULL COMMENT '昵称',
    `phone`       VARCHAR(20)           DEFAULT NULL COMMENT '手机号',
    `email`       VARCHAR(100)          DEFAULT NULL COMMENT '邮箱',
    `status`      TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT(20) DEFAULT NULL COMMENT '创建人',
    `update_by`   BIGINT(20) DEFAULT NULL COMMENT '更新人',
    `deleted`     TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY           `idx_phone` (`phone`),
    KEY           `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入测试数据
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `phone`, `email`, `status`)
VALUES (1, 'admin', '$2a$10$XcigeMfToGQ2ogwdEXTFaO7o.2Y4T9K5l2YcVwV9J8I9J6c5zcYh6', '管理员', '13800138000',
        'admin@example.com', 1);
