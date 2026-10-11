-- =====================================================================
-- 电商项目建表语句（与线上 ecommerce_dev 库保持一致）
-- MySQL 8.x / utf8mb4
--
-- 说明：
--   1. 本文件已同步 2026-xx 商家端改造：merchant 表补充联系人字段，
--      product 表增加 merchant_id（归属商家）与 audit_status（上架审核状态），
--      新增 merchant_update 商家资料变更申请表，user 表增加常用收货地址 address。
--   2. 历史遗留的 shop 表已废弃，由 merchant 表取代，见文件末尾。
--   3. 增量升级脚本见 migration_merchant_audit.sql、migration_merchant_update.sql、
--      migration_user_address.sql、migration_order_settle.sql。
--   4. 历史「已完成但未结算」的订单可用 backfill_order_settle.sql 补打款。
-- =====================================================================

-- ---------------------------------------------------------------------
-- 用户表
-- role: ADMIN 管理员 / MERCHANT 商家 / USER 普通用户
-- status: 0 注销 / 1 正常 / 2 禁用
-- ---------------------------------------------------------------------
CREATE TABLE `user`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`      varchar(50)  NOT NULL COMMENT '用户名',
    `password`      varchar(255) NOT NULL COMMENT '密码',
    `role`          varchar(20)  NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN/MERCHANT/USER',
    `balance`       decimal(10, 2) NOT NULL DEFAULT '0.00' COMMENT '余额',
    `status`        int          NOT NULL DEFAULT '1' COMMENT '状态：正常/禁用/注销',
    `register_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `update_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `phone`         varchar(20)           DEFAULT NULL COMMENT '电话号',
    `email`         varchar(255)          DEFAULT NULL COMMENT '邮箱号',
    `name`          varchar(10)           DEFAULT NULL COMMENT '姓名',
    `address`       varchar(200)          DEFAULT NULL COMMENT '常用收货地址',
    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`),
    KEY `idx_role` (`role`),
    KEY `idx_username` (`username`)
) ENGINE = InnoDB COMMENT '用户表' CHARSET = utf8mb4;

-- ---------------------------------------------------------------------
-- 商家表（商家认证申请 + 审核结果）
-- status: 0 审核中 / 1 已通过(商家) / 2 已拒绝 / 3 已撤销
-- 一个用户最多一条记录（user_id 唯一），被拒绝/撤销后可重新提交覆盖
-- ---------------------------------------------------------------------
CREATE TABLE `merchant`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT,
    `user_id`       bigint       NOT NULL COMMENT '关联的用户ID',
    `shop_name`     varchar(50)  DEFAULT NULL COMMENT '店铺名称',
    `contact_name`  varchar(20)  DEFAULT NULL COMMENT '联系人姓名',
    `contact_phone` varchar(20)  DEFAULT NULL COMMENT '联系电话',
    `shop_address`  varchar(100) NOT NULL COMMENT '店铺地址(省-市)',
    `introduction`  varchar(200) DEFAULT NULL COMMENT '店铺简介',
    `status`        int          NOT NULL DEFAULT '0' COMMENT '商家状态:0审核中 1已通过 2已拒绝 3已撤销',
    `create_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `merchant_pk` (`user_id`)
) ENGINE = InnoDB COMMENT '商家表' CHARSET = utf8mb4;

-- ---------------------------------------------------------------------
-- 商家资料变更申请表（商家端改资料 → 管理员审核 → 通过后才写回 merchant）
-- status: 0 待审核 / 1 已通过 / 2 已驳回；user_id 唯一，驳回后可覆盖重提
-- ---------------------------------------------------------------------
CREATE TABLE `merchant_update`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT,
    `user_id`       bigint       NOT NULL COMMENT '商家用户ID',
    `shop_name`     varchar(50)  DEFAULT NULL COMMENT '申请修改后的店铺名称',
    `contact_name`  varchar(20)  DEFAULT NULL COMMENT '申请修改后的联系人姓名',
    `contact_phone` varchar(20)  DEFAULT NULL COMMENT '申请修改后的联系电话',
    `shop_address`  varchar(100) NOT NULL COMMENT '申请修改后的店铺地址(省-市)',
    `introduction`  varchar(200) DEFAULT NULL COMMENT '申请修改后的店铺简介',
    `status`        int          NOT NULL DEFAULT '0' COMMENT '审核状态:0待审核 1已通过 2已驳回',
    `create_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    `update_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `audit_time`    datetime     DEFAULT NULL COMMENT '审核时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `merchant_update_pk` (`user_id`)
) ENGINE = InnoDB COMMENT '商家资料变更申请表' CHARSET = utf8mb4;

-- ---------------------------------------------------------------------
-- 商品分类表（仅管理员可增删改）
-- ---------------------------------------------------------------------
CREATE TABLE `category`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name`        varchar(50)  NOT NULL COMMENT '分类名称',
    `description` varchar(200) DEFAULT NULL COMMENT '分类描述',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `status`      int          NOT NULL DEFAULT '1' COMMENT '1正常 0已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`),
    KEY `idx_name` (`name`)
) ENGINE = InnoDB COMMENT '商品分类表' CHARSET = utf8mb4;

-- ---------------------------------------------------------------------
-- 商品表
--   merchant_id  : 所属商家（user.id），NULL 表示平台历史商品
--   status       : 1 上架 / 0 下架（用户端只展示 status=1 且 audit_status=1）
--   audit_status : 0 待审核 / 1 已通过 / 2 已驳回（商家新增或修改后重新进入待审核）
-- ---------------------------------------------------------------------
CREATE TABLE `product`
(
    `id`           bigint         NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `merchant_id`  bigint         DEFAULT NULL COMMENT '所属商家用户ID(关联 user.id)',
    `category_id`  bigint         NOT NULL COMMENT '分类ID',
    `name`         varchar(200)   NOT NULL COMMENT '商品名称',
    `price`        decimal(10, 2) NOT NULL COMMENT '价格',
    `stock`        int            NOT NULL DEFAULT '0' COMMENT '库存',
    `image`        varchar(500)   DEFAULT NULL COMMENT '商品图片',
    `description`  text COMMENT '商品描述',
    `status`       int            NOT NULL DEFAULT '0' COMMENT '状态：1上架/0下架',
    `audit_status` int            NOT NULL DEFAULT '0' COMMENT '上架审核状态:0待审核 1已通过 2已驳回',
    `shelve_time`  datetime       DEFAULT NULL COMMENT '上架时间',
    `create_time`  datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_audit_status` (`audit_status`)
) ENGINE = InnoDB COMMENT '商品表' CHARSET = utf8mb4;

-- ---------------------------------------------------------------------
-- 购物车表
-- ---------------------------------------------------------------------
CREATE TABLE `cart`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    `username`    varchar(50) NOT NULL COMMENT '用户名',
    `product_id`  bigint      NOT NULL COMMENT '商品ID',
    `quantity`    int         NOT NULL DEFAULT '1' COMMENT '数量',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `status`      int         NOT NULL DEFAULT '1',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`username`, `product_id`),
    KEY `idx_user_id` (`username`)
) ENGINE = InnoDB COMMENT '购物车表' CHARSET = utf8mb4;

-- ---------------------------------------------------------------------
-- 订单主表
-- status: 0待支付 1已支付 2已发货 3已完成 4已取消
-- ---------------------------------------------------------------------
CREATE TABLE `order`
(
    `id`           bigint         NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no`     varchar(32)    NOT NULL COMMENT '订单号（唯一）',
    `user_name`    varchar(60)    NOT NULL COMMENT '用户名',
    `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
    `pay_amount`   decimal(10, 2) DEFAULT NULL COMMENT '实付金额（优惠后）',
    `status`       int            NOT NULL DEFAULT '0' COMMENT '订单状态：0待支付 1已支付 2已发货 3已完成 4已取消',
    `address`      varchar(500)   DEFAULT NULL COMMENT '收货地址',
    `remark`       varchar(500)   DEFAULT NULL COMMENT '订单备注',
    `create_time`  datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `pay_time`     datetime       DEFAULT NULL COMMENT '支付时间',
    `deliver_time` datetime       DEFAULT NULL COMMENT '发货时间',
    `finish_time`  datetime       DEFAULT NULL COMMENT '完成时间',
    `settle_time`  datetime       DEFAULT NULL COMMENT '商家结算(打款)时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_name`)
) ENGINE = InnoDB COMMENT '订单主表' CHARSET = utf8mb4;

-- ---------------------------------------------------------------------
-- 订单明细表
-- ---------------------------------------------------------------------
CREATE TABLE `order_item`
(
    `id`            bigint         NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no`      varchar(32)    NOT NULL COMMENT '关联订单号',
    `product_id`    bigint         DEFAULT NULL COMMENT '商品ID',
    `product_name`  varchar(200)   NOT NULL COMMENT '商品名称',
    `product_image` varchar(500)   DEFAULT NULL COMMENT '商品图片',
    `price`         decimal(10, 2) NOT NULL COMMENT '商品单价',
    `quantity`      int            NOT NULL COMMENT '购买数量',
    `total_amount`  decimal(10, 2) NOT NULL COMMENT '商品小计（单价×数量）',
    `create_time`   datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `username`      varchar(50)    NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_order_no` (`order_no`)
) ENGINE = InnoDB COMMENT '订单明细表' CHARSET = utf8mb4;


