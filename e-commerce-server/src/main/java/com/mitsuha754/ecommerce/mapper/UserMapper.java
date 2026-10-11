package com.mitsuha754.ecommerce.mapper;

import org.apache.ibatis.annotations.Param;

import com.mitsuha754.ecommerce.dto.UserProfileUpdateDTO;
import com.mitsuha754.ecommerce.vo.UserProfileVO;

import java.math.BigDecimal;
import java.math.BigInteger;

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

    /**
     * 根据ID查用户名
     */
    String checkUserName(@Param("userId") BigInteger userId);

    /**
     * 根据用户名查id
     */
    Integer checkUserId(@Param("userName") String userName);

    /**
     * 校验用户身份
     */
    String getRoleByName(@Param("userName") String userName);

    /**
     * 修改用户角色（商家审核通过/撤销权限时同步 USER ↔ MERCHANT）
     *
     * @return 影响行数
     */
    int updateRole(@Param("userId") Long userId, @Param("role") String role);

    // ==================== 用户中心 ====================

    /**
     * 查询个人资料（不含密码）
     */
    UserProfileVO getProfile(@Param("userName") String userName);

    /**
     * 修改个人资料（姓名/手机号/邮箱/常用地址）
     *
     * @return 影响行数
     */
    int updateProfile(@Param("userName") String userName,
                      @Param("dto") UserProfileUpdateDTO dto);

    /**
     * 查询密码密文（用于修改密码/注销时校验原密码）
     */
    String getPasswordByName(@Param("userName") String userName);

    /**
     * 修改密码（传入 BCrypt 加密后的密文）
     *
     * @return 影响行数
     */
    int updatePassword(@Param("userName") String userName, @Param("password") String password);

    /**
     * 注销账号（status = 0，注销后无法登录）
     *
     * @return 影响行数
     */
    int deactivate(@Param("userName") String userName);

    /**
     * 增加用户余额（订单确认收货后给商家结算打款）
     *
     * @return 影响行数
     */
    int addBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}
