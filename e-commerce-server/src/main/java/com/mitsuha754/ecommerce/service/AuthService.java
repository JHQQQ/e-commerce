package com.mitsuha754.ecommerce.service;

import com.mitsuha754.ecommerce.dto.LoginDTO;
import com.mitsuha754.ecommerce.dto.RegisterDTO;
import com.mitsuha754.ecommerce.vo.UserVO;

import java.util.Map;

public interface AuthService {

    /**
     * @param loginDTO 用户登录信息
     * @return 返回给Controller
     */
    UserVO login(LoginDTO loginDTO);

    /**
     * @param registerDTO 注册表单
     */
    void register(RegisterDTO registerDTO);

    /**
     * 刷新token
     * @param refreshToken 长期token
     */
    UserVO refreshToken(String refreshToken);

    /**
     * 退出登录清除token数据
     * @param refreshToken 持久Token
     */
    void logout(String refreshToken);

    /**
     * 生成验证码
     */
    Map<String, String> getCode();
}
