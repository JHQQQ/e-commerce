package com.mitsuha754.ecommerce.service.Impl;

import com.mitsuha754.ecommerce.mapper.UserMapper;
import com.mitsuha754.ecommerce.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    UserMapper userMapper;

    @Override
    public BigDecimal getBalance(String userName) {
        return userMapper.getUserBalance(userName);
    }
}
