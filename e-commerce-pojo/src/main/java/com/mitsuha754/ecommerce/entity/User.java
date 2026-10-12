package com.mitsuha754.ecommerce.entity;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class User {
    /**
     * 用户ID
     */
    private Integer id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 姓名
     */
    private String name;

    /**
     * 角色
     */
    private String role;

    /**
     * 电话号码
     */
    private String phoneNumber;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 常用收货地址（用户中心维护，下单时可一键填入）
     */
    private String address;

    /** 余额 */
    @DecimalMin(value = "0", message = "余额不能小于0")
    @DecimalMax(value = "100000", message = "余额不能大于10000")
    @Digits(integer = 5, fraction = 2, message = "余额最多保留2位小数，整数部分最多6位")
    @NotNull
    private BigDecimal balance;

    /**
     * 状态:
     * 0注销
     * 1正常
     * 2异常
     */
    private int status;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

}
