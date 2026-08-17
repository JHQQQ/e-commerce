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
}
