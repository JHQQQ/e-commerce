package com.mitsuha754.ecommerce.entity;

import lombok.Data;

/**
 * 商家表 Entity（对应数据库表：merchant）
 */
@Data
public class Shop {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 关联用户ID（merchant.user_id，唯一）
     */
    private Long ownerId;

    /**
     * 店铺名
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
     * 店铺地址（省-市）
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
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

}
