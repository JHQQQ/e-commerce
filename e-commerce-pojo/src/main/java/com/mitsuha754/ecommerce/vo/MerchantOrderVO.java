package com.mitsuha754.ecommerce.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商家端订单列表 VO
 * 说明：订单状态是整单状态（`order`.status），myAmount/itemCount 只统计本店铺的商品
 */
@Data
public class MerchantOrderVO {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 买家用户名
     */
    private String buyerName;

    /**
     * 本店商品小计金额
     */
    private BigDecimal myAmount;

    /**
     * 本店商品总件数
     */
    private Integer itemCount;

    /**
     * 订单状态：0待付款 1待发货 2已发货 3已完成 4已取消
     */
    private Integer orderStatus;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 买家备注
     */
    private String remark;

    /**
     * 下单时间
     */
    private String createTime;

    /**
     * 发货时间
     */
    private String deliverTime;

    /**
     * 商家结算（打款）时间：买家确认收货后货款入账，为空表示尚未入账
     */
    private String settleTime;

}
