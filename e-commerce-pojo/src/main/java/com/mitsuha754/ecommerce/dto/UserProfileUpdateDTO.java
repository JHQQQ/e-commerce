package com.mitsuha754.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改个人资料参数（用户中心）
 * 说明：登录账号 username 不允许修改（订单/购物车等都以它关联）
 */
@Data
public class UserProfileUpdateDTO {

    /**
     * 姓名
     */
    @Size(max = 10, message = "姓名长度不能超过10个字符")
    private String name;

    /**
     * 手机号（选填，填了就要合法）
     */
    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    /**
     * 邮箱（选填，填了就要合法）
     */
    @Email(message = "邮箱格式错误")
    @Size(max = 255, message = "邮箱过长")
    private String email;

    /**
     * 常用收货地址
     */
    @Size(max = 200, message = "常用地址不能超过200个字符")
    private String address;

}
