package com.mitsuha754.ecommerce.util;

import cn.hutool.core.util.StrUtil;
import com.mitsuha754.ecommerce.exception.BusinessException;
import com.mitsuha754.ecommerce.dto.RegisterDTO;

/**
 * 注册表单校验器（专门校验注册参数）
 * 手机号、邮箱等资料由用户注册后在用户中心补充，此处不做校验
 */
public class RegisterValidatorUtil {

    public static void validate(RegisterDTO dto) {
        // 非空校验
        if (StrUtil.isBlank(dto.getUserName())) {
            throw new BusinessException("用户名不能为空");
        }
        if (StrUtil.isBlank(dto.getPassword())) {
            throw new BusinessException("密码不能为空");
        }

        // 长度校验
        if (dto.getUserName().length() < 3 || dto.getUserName().length() > 16) {
            throw new BusinessException("用户名长度必须在3-16位");
        }
        if (dto.getPassword().length() < 6) {
            throw new BusinessException("密码长度不能少于6位");
        }
    }
}
