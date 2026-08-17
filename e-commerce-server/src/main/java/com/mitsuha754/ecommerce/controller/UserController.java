package com.mitsuha754.ecommerce.controller;

import com.mitsuha754.ecommerce.interceptor.LoginInterceptor;
import com.mitsuha754.ecommerce.result.R;
import com.mitsuha754.ecommerce.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/showBalance")
    public R<BigDecimal> showBalance(){
        // 余额归属当前登录用户，不信任前端传入的用户名
        String userName = LoginInterceptor.requireCurrentUser().getUserName();
        return R.ok(userService.getBalance(userName));
    }

}
