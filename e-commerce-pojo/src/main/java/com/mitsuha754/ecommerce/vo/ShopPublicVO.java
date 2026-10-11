package com.mitsuha754.ecommerce.vo;

import lombok.Data;

/**
 * 店铺公开信息 VO（商品详情页「查看商家信息」用）
 * 注意：contactName 是脱敏后的姓名（只保留姓氏），敏感的原值不会返回给前端
 */
@Data
public class ShopPublicVO {

    /**
     * 商家用户ID
     */
    private Long merchantId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺地址
     */
    private String shopAddress;

    /**
     * 店铺简介
     */
    private String introduction;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系人姓名（已脱敏，只显示姓）
     */
    private String contactName;

}
