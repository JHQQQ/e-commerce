package com.mitsuha754.ecommerce.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderVO {
    /**
     * 订单编号
     */
    private String orderNo;
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

}
