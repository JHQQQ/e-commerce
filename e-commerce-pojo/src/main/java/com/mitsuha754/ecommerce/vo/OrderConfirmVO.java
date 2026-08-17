package com.mitsuha754.ecommerce.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单确认VO（结算预览使用）
 *
 * @author mitsuha754
 * &#064;date  2026-04-05
 */
@Data
public class OrderConfirmVO {

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 购买数量
     */
    private String quantity;

    /**
     * 购买单价
     */
    private BigDecimal price;

    /**
     * 商品总价
     */
    private BigDecimal totalPrice;

}
