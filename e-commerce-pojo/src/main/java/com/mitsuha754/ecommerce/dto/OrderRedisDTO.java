package com.mitsuha754.ecommerce.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderRedisDTO implements Serializable {
    private String orderNo;       // 订单号
    private String userName;       // 用户名
    private BigDecimal totalAmount; // 总价
    private Integer status;       // 0-待支付
}