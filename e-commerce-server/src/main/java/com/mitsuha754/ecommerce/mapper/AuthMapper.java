package com.mitsuha754.ecommerce.mapper;

import com.mitsuha754.ecommerce.entity.User;
import org.apache.ibatis.annotations.Param;

public interface AuthMapper {

    /**
     * @param userName 登录表单传来的用户名,
     * @return User类
     */
    User login(@Param("userName") String userName);

    /**
     * 注册（只保存用户名与密码，其余资料由用户中心补充）
     * @param userName 用户名
     * @param password 密码
     */
    void register(@Param("userName") String userName,
                  @Param("password") String password);

    /**
     * @param userName 被检查的用户名
     * @return 是否已经存在
     */
    boolean checkUsername(@Param("userName") String userName);

    /**
     * 通过id查用户名
     * @param userId 用户id
     * @return 用户名
     */
    String getUserNameById(@Param("userId") Integer userId);

}
