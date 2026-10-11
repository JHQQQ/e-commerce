package com.mitsuha754.ecommerce.service.Impl;

import com.mitsuha754.ecommerce.dto.UpdatePasswordDTO;
import com.mitsuha754.ecommerce.dto.UserProfileUpdateDTO;
import com.mitsuha754.ecommerce.exception.BusinessException;
import com.mitsuha754.ecommerce.mapper.UserMapper;
import com.mitsuha754.ecommerce.service.UserService;
import com.mitsuha754.ecommerce.vo.UserProfileVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;

    @Override
    public BigDecimal getBalance(String userName) {
        return userMapper.getUserBalance(userName);
    }

    // ==================== 用户中心 ====================

    @Override
    public UserProfileVO getProfile(String userName) {
        UserProfileVO profile = userMapper.getProfile(userName);
        if (profile == null) {
            throw new BusinessException("用户不存在");
        }
        return profile;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(String userName, UserProfileUpdateDTO dto) {
        // 空串统一转成 null，避免把「未填写」存成空字符串
        dto.setName(trimToNull(dto.getName()));
        dto.setPhone(trimToNull(dto.getPhone()));
        dto.setEmail(trimToNull(dto.getEmail()));
        dto.setAddress(trimToNull(dto.getAddress()));

        int rows = userMapper.updateProfile(userName, dto);
        if (rows == 0) {
            throw new BusinessException("资料保存失败，请刷新后重试");
        }
        log.info("用户[{}]更新个人资料", userName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(String userName, UpdatePasswordDTO dto) {
        String encoded = requirePasswordMatch(userName, dto.getOldPassword());

        if (BCrypt.checkpw(dto.getNewPassword(), encoded)) {
            throw new BusinessException("新密码不能与原密码相同");
        }

        int rows = userMapper.updatePassword(userName, BCrypt.hashpw(dto.getNewPassword(), BCrypt.gensalt()));
        if (rows == 0) {
            throw new BusinessException("密码修改失败，请稍后重试");
        }
        log.info("用户[{}]修改密码成功", userName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deactivate(String userName, String password) {
        requirePasswordMatch(userName, password);

        int rows = userMapper.deactivate(userName);
        if (rows == 0) {
            throw new BusinessException("注销失败，请稍后重试");
        }
        log.info("用户[{}]已注销账号", userName);
    }

    /**
     * 校验密码：返回数据库里的密文，校验不通过直接抛业务异常
     */
    private String requirePasswordMatch(String userName, String rawPassword) {
        if (!StringUtils.hasText(rawPassword)) {
            throw new BusinessException("请输入密码");
        }
        String encoded = userMapper.getPasswordByName(userName);
        if (encoded == null) {
            throw new BusinessException("用户不存在");
        }
        if (!BCrypt.checkpw(rawPassword, encoded)) {
            throw new BusinessException("密码错误");
        }
        return encoded;
    }

    /**
     * 去掉首尾空格，空串返回 null
     */
    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
