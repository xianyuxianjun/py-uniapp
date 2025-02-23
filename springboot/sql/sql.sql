CREATE TABLE `file` (
                         `id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE,
                         `file_name` VARCHAR(255) COMMENT '文件名',
                         `file_path` VARCHAR(255) COMMENT '文件路径',
                         `related_type` VARCHAR(255) COMMENT '文件类型',
                         `related_id` BIGINT,
                         `created_at` DATETIME,
                         PRIMARY KEY(`id`)
) COMMENT='文件表';

CREATE TABLE user (
                       id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID，主键，无符号整数，自动递增',
                       username VARCHAR(255) UNIQUE NOT NULL COMMENT '用户名，唯一且不能为空',
                       password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希值，存储加密后的密码，不能为空',
                       email VARCHAR(255) UNIQUE NOT NULL COMMENT '电子邮件地址，唯一且不能为空',
                       phone VARCHAR(20) UNIQUE COMMENT '电话号码，唯一，允许为空',
                       avatar VARCHAR(255) COMMENT '用户头像的URL或路径，允许为空',
                       status VARCHAR(255) COMMENT '用户状态（如：active, inactive, banned等）',
                       role VARCHAR(255) COMMENT '用户角色（如：admin, user, recycler）',
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间，默认为当前时间',
                       updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间，自动更新为当前时间',
                       gender VARCHAR(255) COMMENT '用户性别（如：男, 女, 未知）'
);
