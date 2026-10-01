package com.mitsuha754.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {
    /**
     * 用户名
     */
    @NotEmpty(message = "用户名不能为空")
    @Size(min = 3, max = 16,message = "用户名必须为3-16位")
    private String userName;
    /**
     * 密码
     */
    @NotEmpty(message = "密码不能为空")
    @Size(min = 6,max = 20,message = "密码长度必须在6-20")
    private String password;
    /**
     * 姓名
     */
    private String name;
    /**
     * 验证码UUID
     */
    @NotEmpty(message = "验证码校验异常")
    private String verify;
    /**
     * 验证码
     */
    private String registerCaptcha ;
    /**
     * 电话号码
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phoneNumber;
    /**
     * 邮箱
     */
    @Email
    private String email;
}
