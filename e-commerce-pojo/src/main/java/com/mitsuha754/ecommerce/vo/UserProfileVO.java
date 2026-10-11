package com.mitsuha754.ecommerce.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户个人资料 VO（用户中心展示用）
 * 注意：不包含密码等敏感字段
 */
@Data
public class UserProfileVO {

    /**
     * 用户ID
     */
    private Integer id;

    /**
     * 登录账号（不支持修改）
     */
    private String userName;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 常用收货地址
     */
    private String address;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 角色：ADMIN / MERCHANT / USER
     */
    private String role;

    /**
     * 状态：0注销 1正常 2禁用
     */
    private Integer status;

    /**
     * 注册时间
     */
    private String createTime;

}
