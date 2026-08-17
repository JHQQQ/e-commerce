package com.mitsuha754.ecommerce.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserVO {

    private Integer userId;

    private String userName;

    private String role;

    private BigDecimal balance;

    //鉴权用的token
    private String accessToken;

    //记住登录后存储cookie的token
    private String refreshToken;

}
