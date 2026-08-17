package com.mitsuha754.ecommerce.dto;

import lombok.Data;

@Data
public class CartDTO {
    private String userName;
    private Long productId;
    private Integer quantity;
}
