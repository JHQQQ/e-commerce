package com.mitsuha754.ecommerce.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 商家修改店铺资料参数（提交后需管理员审核）
 */
@Data
public class MerchantUpdateDTO {

    /**
     * 店铺名称（不填则沿用原名）
     */
    @Size(max = 50, message = "店铺名长度不能超过50个字符")
    private String shopName;

    /**
     * 联系人姓名
     */
    @NotEmpty(message = "联系人姓名不能为空")
    @Size(max = 10, message = "姓名长度不能超过10个字符")
    private String contactName;

    /**
     * 联系电话
     */
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String contactPhone;

    /**
     * 店铺地址（省-市）
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
