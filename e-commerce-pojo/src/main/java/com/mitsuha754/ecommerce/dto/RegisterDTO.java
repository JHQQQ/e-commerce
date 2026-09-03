package com.mitsuha754.ecommerce.dto;

import lombok.Data;

@Data
public class RegisterDTO {
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
     * 验证码UUID
     */
    private String verify;
    /**
     * 验证码
     */
    private String registerCaptcha ;
    /**
     * 电话号码
     */
    private String phoneNumber;
    /**
     * 邮箱
     */
    private String email;
}
