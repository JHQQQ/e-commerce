package com.mitsuha754.ecommerce.mapper;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface UserMapper {
    /**
     * 检验用户状态是否合法
     * @param userName 用户名
     * @return 用户状态
     */
    Integer checkUserStatus(@Param("userName")String  userName);

    /**
     * 原子操作：扣减余额（同时校验用户状态和余额是否充足）
     * @param userName 用户名
     * @param orderNo 订单号（用于获取支付金额）
     * @return 影响行数（1=成功，0=失败）
     */
    int deductBalanceWithCheck(@Param("userName") String userName,
                               @Param("orderNo") String orderNo);

    /**
     * 查看余额
     */
    BigDecimal getUserBalance(@Param("userName")String userName);


}
