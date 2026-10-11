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
     * 所属商家用户ID（NULL 表示平台历史商品）
     */
    private Long merchantId;

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
     * 商品状态（1上架 / 0下架）
     */
    private Integer status;

    /**
     * 上架审核状态（0待审核 1已通过 2已驳回）
     */
    private Integer auditStatus;

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

    /**
     * 所属店铺名称（非数据库字段，用户端列表联表查出）
     */
    private String shopName;

}