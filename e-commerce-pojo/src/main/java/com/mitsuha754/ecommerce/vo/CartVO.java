package com.mitsuha754.ecommerce.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartVO {
    private String userName;
    private Long productId;
    private Integer quantity;
    private Integer status;

    //在mybatis里面查
    private String productImage;
    private String productName;
    private BigDecimal price;
    private BigDecimal totalPrice;

    /**
     * 商品所属商家用户ID（null 表示平台历史商品），用于前端标记「自己的商品」
     */
    private Long merchantId;
}
