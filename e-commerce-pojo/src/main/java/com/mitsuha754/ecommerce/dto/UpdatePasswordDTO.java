package com.mitsuha754.ecommerce.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改密码参数（必须提供原密码）
 */
@Data
public class UpdatePasswordDTO {

    /**
     * 原密码
     */
    @NotEmpty(message = "请输入原密码")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotEmpty(message = "请输入新密码")
    @Size(min = 6, max = 20, message = "新密码长度需在6-20位之间")
    private String newPassword;

}
