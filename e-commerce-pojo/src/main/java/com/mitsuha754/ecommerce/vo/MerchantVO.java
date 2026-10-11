package com.mitsuha754.ecommerce.vo;

import lombok.Data;

/**
 * 商家信息 VO（管理端商家管理页 / 商家端个人信息页）
 */
@Data
public class MerchantVO {

    /**
     * 商家记录ID
     */
    private Long id;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 店铺地址
     */
    private String shopAddress;

    /**
     * 店铺简介
     */
    private String introduction;

    /**
     * 状态(0:审核中 1:已通过 2:已拒绝 3:已撤销)
     */
    private Integer status;

    /**
     * 申请时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

}
