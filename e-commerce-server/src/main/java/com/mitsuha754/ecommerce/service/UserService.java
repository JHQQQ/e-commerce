package com.mitsuha754.ecommerce.service;

import java.math.BigDecimal;

public interface UserService {

    /**
     * 查看余额
     */
    BigDecimal getBalance(String userName);

}
