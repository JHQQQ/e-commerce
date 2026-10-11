package com.mitsuha754.ecommerce.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商家商品 VO（管理端商品审核列表）
 */
@Data
public class MerchantProductVO {

    private Long id;

    /**
     * 所属商家用户ID
     */
    private Long merchantId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 商家用户名
     */
    private String userName;

    private Long categoryId;

    private String categoryName;

    private String name;

    private BigDecimal price;

    private Integer stock;

    private String image;

    private String description;

    /**
     * 上下架状态：1上架 0下架
     */
    private Integer status;

    /**
     * 审核状态：0待审核 1已通过 2已驳回
     */
    private Integer auditStatus;

    private String createTime;

    private String updateTime;

}
