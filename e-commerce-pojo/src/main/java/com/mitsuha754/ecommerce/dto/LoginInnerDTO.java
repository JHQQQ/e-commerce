package com.mitsuha754.ecommerce.dto;

import com.mitsuha754.ecommerce.vo.UserLoginVO;
import lombok.Data;

@Data
public class LoginInnerDTO {

    private UserLoginVO userLoginVO;

    //记住登录后存储cookie的token
    private String refreshToken;
}
