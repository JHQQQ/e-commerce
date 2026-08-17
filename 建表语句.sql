create table cart
(
    id          bigint auto_increment comment '购物车ID'
        primary key,
    username    varchar(50)                        not null comment '用户ID',
    product_id  bigint                             not null comment '商品ID',
    quantity    int      default 1                 not null comment '数量',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    status      int      default 1                 not null,
    constraint uk_user_product
        unique (username, product_id)
)
    comment '购物车表' charset = utf8mb4;

create index idx_user_id
    on cart (username);

create table category
(
    id          bigint auto_increment comment '分类ID'
        primary key,
    name        varchar(50)                        not null comment '分类名称',
    description varchar(200)                       null comment '分类描述',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    status      int      default 1                 not null,
    constraint name
        unique (name)
)
    comment '商品分类表' charset = utf8mb4;

create index idx_name
    on category (name);

create table `order`
(
    id           bigint auto_increment comment '主键ID'
        primary key,
    order_no     varchar(32)                        not null comment '订单号（唯一）',
    user_name    varchar(60)                        not null comment '用户名',
    total_amount decimal(10, 2)                     not null comment '订单总金额',
    pay_amount   decimal(10, 2)                     null comment '实付金额（优惠后）',
    status       int      default 0                 not null comment '订单状态：0待支付 1已支付 2已发货 3已完成 4已取消',
    address      varchar(500)                       null comment '收货地址',
    remark       varchar(500)                       null comment '订单备注',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    pay_time     datetime                           null comment '支付时间',
    deliver_time datetime                           null comment '发货时间',
    finish_time  datetime                           null comment '完成时间',
    constraint uk_order_no
        unique (order_no)
)
    comment '订单主表' charset = utf8mb4;

create index idx_user_id
    on `order` (user_name);

create table order_item
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    order_no      varchar(32)                        not null comment '关联订单号',
    product_id    bigint                             null comment '商品ID',
    product_name  varchar(200)                       not null comment '商品名称',
    product_image varchar(500)                       null comment '商品图片',
    price         decimal(10, 2)                     not null comment '商品单价',
    quantity      int                                not null comment '购买数量',
    total_amount  decimal(10, 2)                     not null comment '商品小计（单价×数量）',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    username      varchar(50)                        not null
)
    comment '订单明细表' charset = utf8mb4;

create index idx_order_no
    on order_item (order_no);

create table product
(
    id          bigint auto_increment comment '商品ID'
        primary key,
    category_id bigint                             not null comment '分类ID',
    name        varchar(200)                       not null comment '商品名称',
    price       decimal(10, 2)                     not null comment '价格',
    stock       int      default 0                 not null comment '库存',
    image       varchar(500)                       null comment '商品图片',
    description text                               null comment '商品描述',
    status      int      default 0                 not null comment '状态：上架/下架',
    shelve_time datetime                           null comment '上架时间',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '商品表' charset = utf8mb4;

create index idx_category_id
    on product (category_id);

create index idx_status
    on product (status);

create table user
(
    id            bigint auto_increment comment '用户ID'
        primary key,
    username      varchar(50)                              not null comment '用户名',
    password      varchar(255)                             not null comment '密码',
    role          varchar(20)    default 'USER'            not null comment '角色：admin/customer',
    balance       decimal(10, 2) default 0.00              not null comment '余额',
    status        int            default 1                 not null comment '状态：正常/禁用/注销',
    register_time datetime       default CURRENT_TIMESTAMP not null comment '注册时间',
    update_time   datetime       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    phone         varchar(20)                              null comment '电话号',
    email         varchar(255)                             null comment '邮箱号',
    name          varchar(10)                              null comment '姓名',
    constraint username
        unique (username)
)
    comment '用户表' charset = utf8mb4;

create index idx_role
    on user (role);

create index idx_username
    on user (username);

