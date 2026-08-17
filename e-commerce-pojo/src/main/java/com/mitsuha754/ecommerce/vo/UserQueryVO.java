package com.mitsuha754.ecommerce.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class UserQueryVO {
    private BigInteger id;
    private String userName;
    private String role;
    private String name;
    private String email;
    private String phone;
    private Integer status;
    private BigDecimal balance;
    private String createTime;
    private String updateTime;

}
