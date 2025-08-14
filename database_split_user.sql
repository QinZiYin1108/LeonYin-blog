-- 个人博客系统数据库设计（拆分用户表版本）
-- 解决双向外键依赖问题

CREATE DATABASE `leon_blog` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `leon_blog`;

-- ========================================
-- 第一步：创建基础表（按依赖顺序）
-- ========================================

-- 1. 用户账号表（基础表，只包含登录认证相关信息）
CREATE TABLE `user_account` (
    `id` VARCHAR(32) PRIMARY KEY COMMENT '用户账号ID，U+雪花ID',
    `email` VARCHAR(255) NOT NULL UNIQUE COMMENT '邮箱',
    `password` VARCHAR(255) COMMENT '密码（微信用户可为空）',
    `user_type` INT NOT NULL DEFAULT 0 COMMENT '用户类型：0-普通用户，1-管理员',
    `status` INT NOT NULL DEFAULT 1 COMMENT '账号状态：0-禁用，1-正常',
    `register_type` INT NOT NULL COMMENT '注册方式：0-邮箱注册',
    `email_verified` INT NOT NULL DEFAULT 0 COMMENT '邮箱验证：0-未验证，1-已验证',
    `last_login_time` BIGINT COMMENT '最后登录时间',
    `create_time` BIGINT NOT NULL COMMENT '创建时间',
    `update_time` BIGINT NOT NULL COMMENT '更新时间',
    INDEX `idx_email` (`email`),
    INDEX `idx_user_type` (`user_type`),
    INDEX `idx_status` (`status`)
) COMMENT='用户账号表';

CREATE TABLE `file_storage` (
    `id` VARCHAR(64) PRIMARY KEY COMMENT '文件ID，FS+雪花ID',
    `file_url` VARCHAR(500) NOT NULL COMMENT '文件访问URL',
    `folder` VARCHAR(128) COMMENT '业务目录（如 images/documents 等）',
    `usage_type` INT NOT NULL DEFAULT 0 COMMENT '用途类型：0-通用，1-头像，2-文章封面，3-分类图标',
    `upload_user_id` VARCHAR(64) COMMENT '上传用户ID',
    `file_name` VARCHAR(255) COMMENT '原始文件名',
    `content_type` VARCHAR(100) COMMENT 'MIME类型',
    `size` BIGINT COMMENT '文件大小（字节）',
    `create_time` BIGINT NOT NULL COMMENT '创建时间',
    `update_time` BIGINT NOT NULL COMMENT '更新时间',
    FOREIGN KEY (`upload_user_id`) REFERENCES `user_account`(`id`) ON DELETE SET NULL,
    INDEX `idx_upload_user` (`upload_user_id`),
    INDEX `idx_usage_type` (`usage_type`),
    INDEX `idx_create_time` (`create_time` DESC)
) COMMENT='文件库表';

-- 3. 图片库表（依赖file_storage表）
CREATE TABLE `image_storage` (
    `id` VARCHAR(32) PRIMARY KEY COMMENT '图片ID，IMG+雪花ID',
    `file_id` VARCHAR(64) NOT NULL COMMENT '对应文件库ID',
    `width` INT COMMENT '图片宽度',
    `height` INT COMMENT '图片高度',
    `usage_type` INT NOT NULL DEFAULT 0 COMMENT '用途类型：0-通用',
    `thumbnail_url` VARCHAR(500) COMMENT '缩略图URL',
    `medium_url` VARCHAR(500) COMMENT '中等尺寸图片URL',
    `alt_text` VARCHAR(200) COMMENT '图片描述文本',
    `exif_data` JSON COMMENT 'EXIF信息',
    `color_palette` JSON COMMENT '主要颜色信息',
    `is_compressed` INT NOT NULL DEFAULT 0 COMMENT '是否已压缩：0-否，1-是',
    `compress_quality` INT DEFAULT 80 COMMENT '压缩质量（1-100）',
    `create_time` BIGINT NOT NULL COMMENT '创建时间',
    `update_time` BIGINT NOT NULL COMMENT '更新时间',
    FOREIGN KEY (`file_id`) REFERENCES `file_storage`(`id`) ON DELETE CASCADE,
    INDEX `idx_file_id` (`file_id`),
    INDEX `idx_dimensions` (`width`, `height`)
) COMMENT='图片库表';

-- 4. 用户信息表（依赖user_account和image_storage表）
CREATE TABLE `user_profile` (
    `id` VARCHAR(32) PRIMARY KEY COMMENT '用户信息ID，UP+雪花ID',
    `user_id` VARCHAR(32) NOT NULL UNIQUE COMMENT '关联的用户账号ID',
    `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
    `avatar_image_id` VARCHAR(32) COMMENT '头像图片ID（关联image_storage表）',
    `bio` VARCHAR(200) COMMENT '个人简介',
    `phone` VARCHAR(11) COMMENT '手机号',
    `create_time` BIGINT NOT NULL COMMENT '创建时间',
    `update_time` BIGINT NOT NULL COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user_account`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`avatar_image_id`) REFERENCES `image_storage`(`id`) ON DELETE SET NULL,
    INDEX `idx_user_account` (`user_id`),
    INDEX `idx_avatar_image` (`avatar_image_id`),
    INDEX `idx_nickname` (`nickname`)
) COMMENT='用户信息表';

-- 5. 文章分类表
CREATE TABLE `category` (
    `id` VARCHAR(32) PRIMARY KEY COMMENT '分类ID，C+雪花ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `description` VARCHAR(200) COMMENT '分类描述',
    `icon_image_id` VARCHAR(32) COMMENT '分类图标ID（关联image_storage表）',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序权重',
    `status` INT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` BIGINT NOT NULL COMMENT '创建时间',
    `update_time` BIGINT NOT NULL COMMENT '更新时间',
    FOREIGN KEY (`icon_image_id`) REFERENCES `image_storage`(`id`) ON DELETE SET NULL,
    INDEX `idx_sort_order` (`sort_order`),
    INDEX `idx_icon_image` (`icon_image_id`)
) COMMENT='文章分类表';

-- 6. 博客文章表
CREATE TABLE `article` (
    `id` VARCHAR(32) PRIMARY KEY COMMENT '文章ID，A+雪花ID',
    `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
    `summary` VARCHAR(500) COMMENT '文章摘要',
    `content` LONGTEXT NOT NULL COMMENT '文章内容',
    `cover_image_id` VARCHAR(32) COMMENT '封面图片ID（关联image_storage表）',
    `author_id` VARCHAR(32) NOT NULL COMMENT '作者ID（关联user_account表）',
    `category_id` VARCHAR(32) NOT NULL COMMENT '分类ID',
    `tags` JSON COMMENT '标签数组',
    `status` INT NOT NULL DEFAULT 1 COMMENT '文章状态：0-下架，1-正常',
    `is_top` INT NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
    `publish_time` BIGINT NOT NULL COMMENT '发布时间',
    `create_time` BIGINT NOT NULL COMMENT '创建时间',
    `update_time` BIGINT NOT NULL COMMENT '更新时间',
    FOREIGN KEY (`author_id`) REFERENCES `user_account`(`id`),
    FOREIGN KEY (`category_id`) REFERENCES `category`(`id`),
    FOREIGN KEY (`cover_image_id`) REFERENCES `image_storage`(`id`) ON DELETE SET NULL,
    INDEX `idx_author` (`author_id`),
    INDEX `idx_category` (`category_id`),
    INDEX `idx_cover_image` (`cover_image_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_publish_time` (`publish_time` DESC)

) COMMENT='博客文章表';

-- 新增：文章热度表
CREATE TABLE `article_heat` (
    `id` VARCHAR(32) PRIMARY KEY COMMENT '热度ID，H+雪花ID',
    `article_id` VARCHAR(32) NOT NULL COMMENT '文章ID',
    `view_count` BIGINT NOT NULL DEFAULT 0 COMMENT '浏览量',
    `like_count` BIGINT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `collect_count` BIGINT NOT NULL DEFAULT 0 COMMENT '收藏数',
    `hot_score` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '热度分数',
    `publish_time` BIGINT COMMENT '发布时间（用于时间衰减）',
    `create_time` BIGINT NOT NULL COMMENT '创建时间',
    `update_time` BIGINT NOT NULL COMMENT '更新时间',
    FOREIGN KEY (`article_id`) REFERENCES `article`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_article_id` (`article_id`),
    INDEX `idx_article_id` (`article_id`),
    INDEX `idx_hot_score_pub` (`hot_score`, `publish_time`)
) COMMENT='文章热度表';

-- 7. 文章评论表（已废弃）

-- 8. 用户点赞表
CREATE TABLE `user_like` (
    `id` VARCHAR(32) PRIMARY KEY COMMENT 'ID，L+雪花ID',
    `user_id` VARCHAR(32) NOT NULL COMMENT '用户ID（关联user_account表）',
    `target_type` INT NOT NULL COMMENT '目标类型：0-文章（与代码一致）',
    `target_id` VARCHAR(32) NOT NULL COMMENT '目标ID',
    `create_time` BIGINT NOT NULL COMMENT '创建时间',
    `update_time` BIGINT NOT NULL COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user_account`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
    INDEX `idx_user` (`user_id`),
    INDEX `idx_target` (`target_type`, `target_id`)
) COMMENT='用户点赞表';

-- 9. 用户收藏表
CREATE TABLE `user_collect` (
    `id` VARCHAR(32) PRIMARY KEY COMMENT 'ID，CO+雪花ID',
    `user_id` VARCHAR(32) NOT NULL COMMENT '用户ID（关联user_account表）',
    `article_id` VARCHAR(32) NOT NULL COMMENT '文章ID',
    `create_time` BIGINT NOT NULL COMMENT '创建时间',
    `update_time` BIGINT NOT NULL COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user_account`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`article_id`) REFERENCES `article`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_user_article` (`user_id`, `article_id`),
    INDEX `idx_user` (`user_id`),
    INDEX `idx_article` (`article_id`)
) COMMENT='用户收藏表';

-- 10. 用户操作日志表
CREATE TABLE `user_log` (
    `id` VARCHAR(32) PRIMARY KEY COMMENT 'ID，LOG+雪花ID',
    `user_id` VARCHAR(32) COMMENT '用户ID（关联user_account表，游客可为空）',
    `action` VARCHAR(50) NOT NULL COMMENT '操作类型',
    `target_type` VARCHAR(20) COMMENT '目标类型',
    `target_id` VARCHAR(32) COMMENT '目标ID',
    `ip_address` VARCHAR(45) COMMENT 'IP地址',
    `user_agent` VARCHAR(500) COMMENT '用户代理',
    `details` TEXT COMMENT '操作详情（JSON格式）',
    `create_time` BIGINT NOT NULL COMMENT '创建时间',
    `update_time` BIGINT NOT NULL COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user_account`(`id`) ON DELETE SET NULL,
    INDEX `idx_user` (`user_id`),
    INDEX `idx_action` (`action`),
    INDEX `idx_create_time` (`create_time`)
) COMMENT='用户操作日志表';

-- 11. 用户设备表
CREATE TABLE `user_device` (
    `id` VARCHAR(32) PRIMARY KEY COMMENT '设备记录ID，UD+雪花ID',
    `user_id` VARCHAR(32) NOT NULL COMMENT '用户ID（关联user_account表）',
    `device_id` VARCHAR(100) NOT NULL COMMENT '设备标识',
    `device_info` VARCHAR(500) COMMENT '设备信息（浏览器、操作系统等）',
    `ip_address` VARCHAR(45) COMMENT 'IP地址',
    `location` VARCHAR(100) COMMENT '登录地点',
    `last_login_time` BIGINT NOT NULL COMMENT '最后登录时间',
    `status` INT NOT NULL DEFAULT 1 COMMENT '状态：0-已注销，1-正常',
    `create_time` BIGINT NOT NULL COMMENT '创建时间',
    `update_time` BIGINT NOT NULL COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user_account`(`id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_device_id` (`device_id`),
    INDEX `idx_user_device` (`user_id`, `device_id`)
) COMMENT='用户设备表';

-- 12. 系统配置表
CREATE TABLE `system_config` (
    `id` VARCHAR(32) PRIMARY KEY COMMENT 'ID，SC+雪花ID',
    `config_key` VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    `config_value` TEXT COMMENT '配置值',
    `description` VARCHAR(200) COMMENT '配置描述',
    `config_type` VARCHAR(20) NOT NULL DEFAULT 'string' COMMENT '配置类型：string,number,boolean,json',
    `category` VARCHAR(50) NOT NULL DEFAULT 'general' COMMENT '配置分类',
    `is_public` INT NOT NULL DEFAULT 0 COMMENT '是否公开：0-否，1-是',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序权重',
    `status` INT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` BIGINT NOT NULL COMMENT '创建时间',
    `update_time` BIGINT NOT NULL COMMENT '更新时间',
    INDEX `idx_config_key` (`config_key`),
    INDEX `idx_category` (`category`),
    INDEX `idx_is_public` (`is_public`),
    INDEX `idx_sort_order` (`sort_order`),
    INDEX `idx_status` (`status`)
) COMMENT='系统配置表';








