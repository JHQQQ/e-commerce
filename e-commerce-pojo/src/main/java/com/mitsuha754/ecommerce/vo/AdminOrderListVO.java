package com.mitsuha754.ecommerce.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminOrderListVO {
    private String orderNo;
    private String userName;
    private BigDecimal totalPrice;
    private int orderStatus;
    private String remark;
    private String createTime;
}
