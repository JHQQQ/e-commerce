package com.mitsuha754.ecommerce.enums;

import lombok.Getter;

/**
 * 订单状态枚举
 * 对应 Order 实体类中的 status 字段
 */
@Getter
public enum OrderStatusEnum {

    // 0：待付款 1：待发货 2：已发货 3：已完成 4：已取消
    PENDING_PAY(0, "待付款"),
    PENDING_DELIVER(1, "待发货"),
    DELIVERED(2, "已发货"),
    COMPLETED(3, "已完成"),
    CANCELED(4, "已取消");

    private final Integer code;
    private final String desc;

    OrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}