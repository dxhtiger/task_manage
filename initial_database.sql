-- --------------------------------------------
-- 基础环境
-- --------------------------------------------
-- 如需新建库：
CREATE DATABASE IF NOT EXISTS taskmanage
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE taskmanage;

-- 推荐的 SQL 模式（可按需调整）
SET SESSION sql_mode='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- --------------------------------------------
-- users 表
-- 说明：
--  - username、email 唯一
--  - role 存 "ROLE_USER" / "ROLE_ADMIN" 等
--  - status 0=禁用 1=启用
--  - is_deleted 逻辑删除
-- --------------------------------------------
DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  username          VARCHAR(64)     NOT NULL COMMENT '用户名',
  password          VARCHAR(255)    NOT NULL COMMENT 'BCrypt 密码哈希',
  email             VARCHAR(128)    NOT NULL COMMENT '邮箱',
  phone             VARCHAR(32)              COMMENT '手机号',
  role              VARCHAR(64)     NOT NULL DEFAULT 'ROLE_USER' COMMENT '角色标识',
  avatar            VARCHAR(512)             COMMENT '头像 URL',
  status            TINYINT         NOT NULL DEFAULT 1 COMMENT '状态: 0禁用,1启用',
  login_fail_count  INT             NOT NULL DEFAULT 0 COMMENT '登录失败次数',
  lock_time         DATETIME                 COMMENT '锁定到期时间',
  is_deleted        TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0否,1是',
  created_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_username (username),
  UNIQUE KEY uk_users_email (email),
  KEY idx_users_status (status),
  KEY idx_users_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- --------------------------------------------
-- tasks 表
-- 说明：
--  - priority: 1=低 2=中 3=高
--  - status: 0=待办 1=进行中 2=已完成（与你的定时器配置一致）
--  - is_deleted 逻辑删除
--  - 为到期查询与分页做了覆盖索引
-- --------------------------------------------
DROP TABLE IF EXISTS tasks;
CREATE TABLE tasks (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  user_id      BIGINT UNSIGNED NOT NULL COMMENT '所属用户',
  title        VARCHAR(255)    NOT NULL COMMENT '标题',
  description  TEXT                     COMMENT '描述',
  priority     TINYINT         NOT NULL DEFAULT 2 COMMENT '优先级:1低,2中,3高',
  deadline     DATETIME                 COMMENT '截止时间',
  status       TINYINT         NOT NULL DEFAULT 0 COMMENT '状态:0待办,1进行中,2已完成',
  is_deleted   TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否,1是',
  created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  -- 生成列：仅日期部分，便于 "今日到期" 类查询用到索引
  deadline_date DATE GENERATED ALWAYS AS (DATE(deadline)) STORED,
  PRIMARY KEY (id),
  CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE RESTRICT,

  -- 索引设计：
  -- 1) 你们常用的分页/过滤：user + status/priority + 创建时间
  KEY idx_tasks_user_status_created (user_id, status, created_at DESC),
  KEY idx_tasks_user_priority_created (user_id, priority, created_at DESC),

  -- 2) 到期任务：使用生成列 deadline_date，让 "deadline_date = '2025-08-14'" 可走索引
  KEY idx_tasks_deadline_date (deadline_date),

  -- 3) 管理员面向全量列表的排序/过滤
  KEY idx_tasks_status_created (status, created_at DESC),

  -- 4) 关键字搜索（标题/描述），MySQL 8.0+ 全文索引
  FULLTEXT KEY ftx_tasks_title_desc (title, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- --------------------------------------------
-- 演示数据
-- 注意：password 建议替换为真实的 BCrypt 哈希
-- 下面的哈希是示例（密码明文: "P@ssw0rd!"），你可以用项目里的 BCryptPasswordEncoder 生成后替换
-- --------------------------------------------
INSERT INTO users (username, password, email, phone, role, status)
VALUES
  ('admin',  '$2a$10$5Zk4g0C3Xf2x/5Lq9mXj0u5w3xWbU6cA2Z8mH.2JtQe9xEo9Jr5Qy', 'admin@example.com', '13800000000', 'ROLE_ADMIN', 1),
  ('alice',  '$2a$10$5Zk4g0C3Xf2x/5Lq9mXj0u5w3xWbU6cA2Z8mH.2JtQe9xEo9Jr5Qy', 'alice@example.com', '13900000001', 'ROLE_USER',  1),
  ('bob',    '$2a$10$5Zk4g0C3Xf2x/5Lq9mXj0u5w3xWbU6cA2Z8mH.2JtQe9xEo9Jr5Qy', 'bob@example.com',   '13900000002', 'ROLE_USER',  1);

INSERT INTO tasks (user_id, title, description, priority, deadline, status, is_deleted, created_at, updated_at)
VALUES
  ((SELECT id FROM users WHERE username='alice'), '写周报',     '整理本周工作内容',          2, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 0 DAY), 0, 0, NOW(), NOW()),
  ((SELECT id FROM users WHERE username='alice'), '接口联调',   '对接订单模块接口',          3, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 DAY), 1, 0, NOW(), NOW()),
  ((SELECT id FROM users WHERE username='bob'),   '代码评审',   'Review PR #123',           2, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 0 DAY), 0, 0, NOW(), NOW()),
  ((SELECT id FROM users WHERE username='bob'),   '修复缺陷',   '修复生产告警的 NPE',        3, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 2 DAY), 0, 0, NOW(), NOW());

-- --------------------------------------------
-- 建议的查询写法（给参考，不必执行）
-- --------------------------------------------
-- 1) 「今日到期」走索引（配合你们的定时任务）
-- SELECT user_id, id AS task_id, title, deadline
-- FROM tasks
-- WHERE is_deleted=0
--   AND deadline_date = CURRENT_DATE()
--   AND status <> 2;

-- 2) 用户自己的分页列表（按状态过滤 + 创建时间排序）
-- SELECT * FROM tasks
-- WHERE user_id = :uid AND is_deleted=0
--   AND (:status IS NULL OR status = :status)
-- ORDER BY created_at DESC
-- LIMIT :offset, :size;

-- 3) 标题/描述关键字
-- SELECT id, user_id, title, description
-- FROM tasks
-- WHERE is_deleted=0
--   AND MATCH(title, description) AGAINST(:kw IN NATURAL LANGUAGE MODE);

