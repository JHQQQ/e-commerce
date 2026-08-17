package com.mitsuha754.ecommerce.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 每个物品的订单
 */
@Data
public class OrderItem {
    private Long id;
    /**
     * 关联的订单号
     */
    private String orderNo;

    /**
     * 商品ID
     */
    private  Long productId;

    /**
     * 商品名
     */
    private String productName;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 下单数量
     */
    private Integer quantity;

    /**
     * 总金额
     */
    private BigDecimal totalPrice;

    /**
     * 创建时间
     */
    private String createTime;
}
