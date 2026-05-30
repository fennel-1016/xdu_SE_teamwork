-- ============================================
-- 饭电项目 - 数据库初始化脚本
-- 用途：项目首次部署时手动执行此 SQL 建库建表
-- ============================================

CREATE DATABASE IF NOT EXISTS fandian
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE fandian;

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    openid          VARCHAR(64)   NOT NULL PRIMARY KEY COMMENT '微信用户唯一标识',
    nickname        VARCHAR(128)  DEFAULT ''  COMMENT '用户微信昵称',
    create_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '账号创建时间',
    last_login_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后登录时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 菜品表
CREATE TABLE IF NOT EXISTS t_food (
    id             INT           NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '菜品自增主键',
    food_name      VARCHAR(128)  NOT NULL COMMENT '菜品名称',
    price          DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格（元）',
    canteen_name   VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '食堂名称',
    window_no      VARCHAR(32)   NOT NULL DEFAULT '' COMMENT '档口/窗口编号',
    sync_time      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '爬虫最后同步时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';
