package com.mitsuha754.ecommerce.entity;

import jakarta.validation.constraints.*;
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
    @Size(min = 1, max = 10, message = "商品名称应该在1-10字")
    private String name;

    /** 商品价格 */
    @DecimalMin(value = "0", message = "商品价格不能小于0")
    @DecimalMax(value = "10000", message = "商品价格不能大于10000")
    @Digits(integer = 5, fraction = 2, message = "价格最多保留2位小数，整数部分最多5位")
    @NotNull
    private BigDecimal price;

    /**
     * 库存数量
     */
    @Max(value = 1000,message = "库存应小于1000")
    @Min(value = 0, message = "库存应大于0")
    @NotNull
    private Integer stock;

    /**
     * 商品图片URL
     */
    private String image;

    /**
     * 商品描述
     */
    @Size(max = 20, message = "简介字数应在1-20之间")
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