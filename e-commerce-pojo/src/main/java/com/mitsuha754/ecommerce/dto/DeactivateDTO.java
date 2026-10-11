package com.mitsuha754.ecommerce.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 注销账号参数（需要密码确认身份）
 */
@Data
public class DeactivateDTO {

    /**
     * 当前密码
     */
    @NotEmpty(message = "请输入密码以确认注销")
    private String password;

}
