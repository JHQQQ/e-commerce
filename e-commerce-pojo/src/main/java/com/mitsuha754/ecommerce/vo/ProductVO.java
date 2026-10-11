package com.mitsuha754.ecommerce.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVO {
    Long id;
    String name;
    String image;
    Long categoryId;
    String categoryName;
    BigDecimal price;
    Integer stock;
    String description;
    /**
     * 所属商家用户ID（null 表示平台历史商品），用于前端判断「是否自己的商品」
     */
    Long merchantId;

    /**
     * 所属店铺名称（商品详情页展示 + 「查看商家信息」入口）
     */
    String shopName;
}
