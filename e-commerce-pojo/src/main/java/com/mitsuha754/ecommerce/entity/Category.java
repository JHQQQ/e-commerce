package com.mitsuha754.ecommerce.entity;

import lombok.Data;

//商品类别
@Data
public class Category {
    /**
     * 主键id
    */
    private Long id;

    /**
     * 商品类别名
     */
    private String name;

    /**
     * 类别描述
     */
    private String description;

    /**
     * 状态(0删除 1正常)
     */
    private int status;

    /**
     * 创建时间
     */
    private String createTime;


}
