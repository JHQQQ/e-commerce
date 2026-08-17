package com.mitsuha754.ecommerce.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品表 Entity
 * 对应数据库表名：product
 */
@Data
public class Product {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 商品图片URL
     */
    private String image;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品状态（如：上架/下架）
     */
    private Integer status;

    /**
     * 上架时间
     */
    private Date shelveTime;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

}