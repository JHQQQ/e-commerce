package com.mitsuha754.ecommerce.service;

import com.mitsuha754.ecommerce.dto.UpdatePasswordDTO;
import com.mitsuha754.ecommerce.dto.UserProfileUpdateDTO;
import com.mitsuha754.ecommerce.vo.UserProfileVO;

import java.math.BigDecimal;

public interface UserService {

    /**
     * 查看余额
     */
    BigDecimal getBalance(String userName);

    // ==================== 用户中心 ====================

    /**
     * 查询个人资料（不含密码）
     */
    UserProfileVO getProfile(String userName);

    /**
     * 修改个人资料（姓名/手机号/邮箱/常用地址）
     */
    void updateProfile(String userName, UserProfileUpdateDTO dto);

    /**
     * 修改密码
     * 必须校验原密码；修改成功后建议前端重新登录
     */
    void updatePassword(String userName, UpdatePasswordDTO dto);

    /**
     * 注销账号
     * 需要校验密码；注销后无法登录、无法续期
     *
     * @param password 当前密码（确认身份）
     */
    void deactivate(String userName, String password);

}
