package com.mitsuha754.ecommerce.dto;


import lombok.Data;

/**
 * 登录请求DTO：字段+校验规则，和前端参数严格对齐
 */
@Data
public class LoginDTO {
    /**
     * 用户名：非空，且长度1-20
     */

    private String userName;

    /**
     * 密码：非空，且长度6-20
     */
    private String password;
    /**
     * 是否记住登录状态
     */
    private Boolean isRemember;

    /**
     * 验证码UUID
     */
    private String verify;

    /**
     * 验证码
     */
    private String loginCaptcha ;

}