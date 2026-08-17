package com.mitsuha754.ecommerce.entity;

import lombok.Data;

@Data
public class Cart {
    private Long id;

    private String userName;

    private String productName;

    private Integer quantity;

    /**
     * 0删除
     * 1添加
     * 2已购买
     */
    private Integer status;

    private String createTime;

    private String updateTime;
}
