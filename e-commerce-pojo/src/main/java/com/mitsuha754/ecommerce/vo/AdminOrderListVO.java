package com.mitsuha754.ecommerce.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminOrderListVO {
    private String orderNo;
    private String userName;
    private BigDecimal totalPrice;
    private int orderStatus;
    private String address;
    private String remark;
    private String createTime;
    /**
     * 商家结算（打款）时间，为空表示尚未结算
     */
    private String settleTime;
}
