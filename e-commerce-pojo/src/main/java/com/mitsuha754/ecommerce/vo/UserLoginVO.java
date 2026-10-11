package com.mitsuha754.ecommerce.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class UserLoginVO {
    private String userName;

    private Integer userId;

    private String role;

    //鉴权用的token
    private String accessToken;


}
