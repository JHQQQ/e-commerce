package com.mitsuha754.ecommerce.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 商家认证申请参数
 */
@Data
public class ApplyMerchantDTO {

    /**
     * 联系人姓名
     */
    @NotEmpty(message = "姓名不能为空")
    @Size(max = 10, message = "姓名长度不能超过10个字符")
    private String name;

    /**
     * 联系电话
     */
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    /**
     * 店铺名（不填则默认「用户名 + 的店铺」）
     */
    @Size(max = 50, message = "店铺名长度不能超过50个字符")
    private String shopName;

    /**
     * 店铺地址（省-市，由前端级联选择器拼接）
     */
    @NotEmpty(message = "店铺地址不能为空")
    @Size(max = 100, message = "店铺地址过长")
    private String shopAddress;

    /**
     * 店铺简介
     */
    @Size(max = 200, message = "店铺简介不能超过200个字符")
    private String introduction;

}
