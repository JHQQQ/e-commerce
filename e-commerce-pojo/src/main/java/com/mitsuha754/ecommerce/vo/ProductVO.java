package com.mitsuha754.ecommerce.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVO {
    String name;
    String image;
    Long categoryId;
    String categoryName;
    BigDecimal price;
    Integer stock;
    String description;
}
