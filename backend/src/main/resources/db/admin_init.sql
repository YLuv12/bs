-- 管理员表
CREATE TABLE IF NOT EXISTS `admin` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(100) NOT NULL,
    `nickname` VARCHAR(50),
    `phone` VARCHAR(20),
    `last_login_time` DATETIME,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME ON UPDATE CURRENT_TIMESTAMP,
    `delete_time` DATETIME
);

-- 操作日志表
CREATE TABLE IF NOT EXISTS `admin_operation_log` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `admin_id` INT NOT NULL,
    `module` VARCHAR(50),
    `operation` VARCHAR(200),
    `target_id` INT,
    `ip` VARCHAR(50),
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 系统配置表
CREATE TABLE IF NOT EXISTS `sys_config` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `config_key` VARCHAR(50) NOT NULL UNIQUE,
    `config_value` TEXT,
    `config_name` VARCHAR(100),
    `config_type` VARCHAR(20) DEFAULT 'string',
    `module` VARCHAR(50),
    `remark` VARCHAR(200),
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME ON UPDATE CURRENT_TIMESTAMP
);

-- 初始化管理员（密码：admin123）
INSERT INTO `admin` (`username`, `password`, `nickname`, `create_time`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '超级管理员', NOW());

-- 初始化系统配置
INSERT INTO `sys_config` (`config_key`, `config_value`, `config_name`, `config_type`, `module`, `remark`, `create_time`) VALUES
('site_name', '百色指南平台', '网站名称', 'string', '系统设置', '网站显示名称', NOW()),
('site_logo', '/uploads/logo.png', '网站Logo', 'string', '系统设置', '网站Logo图片地址', NOW()),
('upload_max_size', '10485760', '上传文件最大大小', 'int', '系统设置', '单位：字节，默认10MB', NOW());
