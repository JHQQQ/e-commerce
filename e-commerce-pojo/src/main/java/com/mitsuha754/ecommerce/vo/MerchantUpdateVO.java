package com.mitsuha754.ecommerce.vo;

import lombok.Data;

/**
 * 商家资料变更申请 VO（管理端审核列表：现有资料 vs 申请资料）
 */
@Data
public class MerchantUpdateVO {

    private Long userId;

    /**
     * 商家用户名
     */
    private String userName;

    /**
     * 审核状态：0 待审核 / 1 已通过 / 2 已驳回
     */
    private Integer status;

    /**
     * 提交时间
     */
    private String createTime;

    /**
     * 审核时间
     */
    private String auditTime;

    // ===== 现有资料（merchant 表）=====

    private String currentShopName;
    private String currentContactName;
    private String currentContactPhone;
    private String currentShopAddress;
    private String currentIntroduction;

    // ===== 申请修改后的资料（merchant_update 表）=====

    private String newShopName;
    private String newContactName;
    private String newContactPhone;
    private String newShopAddress;
    private String newIntroduction;

}
