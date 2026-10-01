package com.mitsuha754.ecommerce.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserLoginVO {


    private String userName;

    private String role;

    //鉴权用的token
    private String accessToken;


}
