package com.mitsuha754.ecommerce.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserPayDTO {
    @NotEmpty(message = "订单号异常")
    private String orderNo;

    @NotEmpty(message = "用户名异常")
    private String userName;
}
