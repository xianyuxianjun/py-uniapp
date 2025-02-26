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

CREATE TABLE waste_category (
                               id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，唯一标识每个分类',
                               category_name VARCHAR(100) NOT NULL COMMENT '分类名称（如"纸类"或"报纸"）',
                               price DECIMAL(10, 2) COMMENT '回收价格（单位：元/公斤或其他）',
                               image_url VARCHAR(255) COMMENT '图片URL（用于展示分类图片）',
                               unit VARCHAR(255) COMMENT '计量单位（如：公斤、个、件等）',
                               description TEXT COMMENT '分类描述（可选，用于补充说明分类信息）',
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间，自动生成',
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间，自动更新'
) COMMENT='废品分类表，用于存储废品分类的名称、价格、图片等信息';

CREATE TABLE appointment_order (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID，主键，自增',
                                   user_id BIGINT NOT NULL COMMENT '用户ID，关联用户表',
                                   appointment_time DATETIME NOT NULL COMMENT '预约时间',
                                   address VARCHAR(255) NOT NULL COMMENT '上门地址',
                                   contact_name VARCHAR(100) NOT NULL COMMENT '联系人姓名',
                                   contact_phone VARCHAR(20) NOT NULL COMMENT '联系人电话',
                                   status TINYINT NOT NULL DEFAULT 1 COMMENT '订单状态（1：待确认，2：已接单，3：已完成，4：已取消）',
                                   notes TEXT COMMENT '备注信息',
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '订单更新时间',
                                   longitude DECIMAL(10, 7) COMMENT '上门地址经度，用于地图展示',
                                   latitude DECIMAL(10, 7) COMMENT '上门地址纬度，用于地图展示',
                                   map_address VARCHAR(255) COMMENT '地图上的标准地址'
) COMMENT='上门预约订单表';

CREATE TABLE announcement (
                              id INT AUTO_INCREMENT PRIMARY KEY COMMENT '唯一标识ID，主键，自增',
                              title VARCHAR(255) NOT NULL COMMENT '新闻或公告标题',
                              content TEXT NOT NULL COMMENT '新闻或公告内容',
                              status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1：草稿，2：发布，3：下线）',
                              publish_date DATETIME COMMENT '发布日期',
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='新闻/公告表';

CREATE TABLE order_review (
                              review_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评价ID，主键，自增',
                              order_id BIGINT NOT NULL COMMENT '订单ID，关联订单表',
                              customer_id BIGINT NOT NULL COMMENT '客户ID，关联用户表',
                              rating TINYINT NOT NULL COMMENT '评分（例如1-5分）',
                              comment TEXT COMMENT '评价文本内容',
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '评价创建时间',
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '评价更新时间'
) COMMENT='订单评价表';

CREATE TABLE recycling_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID，主键，自增',
    order_id BIGINT NOT NULL COMMENT '关联的预约订单ID',
    recycler_id BIGINT NOT NULL COMMENT '回收员ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    category_id BIGINT NOT NULL COMMENT '废品分类ID',
    weight DECIMAL(10, 2) NOT NULL COMMENT '重量/数量',
    unit_price DECIMAL(10, 2) NOT NULL COMMENT '单价',
    amount DECIMAL(10, 2) NOT NULL COMMENT '金额',
    pickup_time DATETIME NOT NULL COMMENT '上门回收时间',
    pickup_address VARCHAR(255) NOT NULL COMMENT '回收地址',
    notes TEXT COMMENT '备注信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='回收记录表';

-- 添加索引
CREATE INDEX idx_record_order ON recycling_record(order_id);
CREATE INDEX idx_record_recycler ON recycling_record(recycler_id);
CREATE INDEX idx_record_customer ON recycling_record(customer_id);
CREATE INDEX idx_record_category ON recycling_record(category_id);
CREATE INDEX idx_record_pickup_time ON recycling_record(pickup_time);

CREATE TABLE product (
                              id bigint AUTO_INCREMENT PRIMARY KEY COMMENT '商品唯一标识',
                              product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
                              description TEXT COMMENT '商品描述',
                              price DECIMAL(10, 2) NOT NULL COMMENT '商品销售价格',
                              stock_quantity INT NOT NULL COMMENT '商品库存数量',
                                image_url VARCHAR(255) COMMENT '商品图片URL',
                             status TINYINT NOT NULL DEFAULT 1 COMMENT '商品状态（1：在售，2：下架）',
                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '商品创建时间',
                              updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '商品更新时间'
) COMMENT='商品信息表';

CREATE TABLE `product_order` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                 `order_no` varchar(64) NOT NULL COMMENT '订单编号',
                                 `user_id` bigint(20) NOT NULL COMMENT '用户ID',
                                 `product_id` bigint(20) NOT NULL COMMENT '商品ID',
                                 `product_name` varchar(128) NOT NULL COMMENT '商品名称',
                                 `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
                                 `price` decimal(10,2) NOT NULL COMMENT '商品单价',
                                 `quantity` int(11) NOT NULL DEFAULT '1' COMMENT '购买数量',
                                 `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
                                 `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '订单状态：1-待支付，2-已支付，3-已发货，4-已完成，5-已取消',
                                 `address` varchar(255) DEFAULT NULL COMMENT '收货地址',
                                 `contact_name` varchar(64) DEFAULT NULL COMMENT '联系人姓名',
                                 `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系人电话',
                                 `notes` varchar(255) DEFAULT NULL COMMENT '订单备注',
                                 `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
                                 `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
                                 `completion_time` datetime DEFAULT NULL COMMENT '完成时间',
                                 `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_order_no` (`order_no`),
                                 KEY `idx_user_id` (`user_id`),
                                 KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品订单表';



