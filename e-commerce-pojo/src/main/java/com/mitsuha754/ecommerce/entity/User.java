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
    /**
     * 余额
     */
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
