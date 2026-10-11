package com.mitsuha754.ecommerce.entity;

import lombok.Data;

/**
 * 商家资料变更申请 Entity（对应数据库表：merchant_update）
 * 商家端提交的店铺资料修改先落在这张表，管理员审核通过后才写回 merchant
 */
@Data
public class MerchantUpdate {

    private Long id;

    /**
     * 商家用户ID（唯一）
     */
    private Long userId;

    /**
     * 申请修改后的店铺名称
     */
    private String shopName;

    /**
     * 申请修改后的联系人姓名
     */
    private String contactName;

    /**
     * 申请修改后的联系电话
     */
    private String contactPhone;

    /**
     * 申请修改后的店铺地址
     */
    private String shopAddress;

    /**
     * 申请修改后的店铺简介
     */
    private String introduction;

    /**
     * 审核状态：0 待审核 / 1 已通过 / 2 已驳回
     */
    private Integer status;

    private String createTime;

    private String updateTime;

    /**
     * 审核时间
     */
    private String auditTime;

}
