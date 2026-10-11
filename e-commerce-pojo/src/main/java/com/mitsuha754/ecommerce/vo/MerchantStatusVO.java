package com.mitsuha754.ecommerce.vo;

import lombok.Data;

/**
 * 当前用户的商家认证状态（商家端入口判断 / 个人信息页展示）
 */
@Data
public class MerchantStatusVO {

    /**
     * 当前用户角色（实时取自数据库：ADMIN / MERCHANT / USER）
     */
    private String role;

    /**
     * 商家状态：null 未申请 / 0审核中 / 1已通过 / 2已拒绝 / 3已撤销
     */
    private Integer status;

    /**
     * 状态中文描述
     */
    private String statusText;

    /**
     * 商家详细信息（未申请时为 null）
     */
    private MerchantVO merchant;

}
