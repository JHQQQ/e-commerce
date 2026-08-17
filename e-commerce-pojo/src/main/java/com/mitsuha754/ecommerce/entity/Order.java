package com.mitsuha754.ecommerce.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 对应数据库表：order（注意：order是SQL关键字，实际建表建议使用orders或t_order）
 */
@Data
public class Order {

    /**
     * 订单ID（主键）
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 实付金额
     */
    private BigDecimal payAmount;

    /**
     * 状态：待付款/待发货/已发货/已完成/已取消(0/1/2/3/4)
     */
    private Integer status;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}