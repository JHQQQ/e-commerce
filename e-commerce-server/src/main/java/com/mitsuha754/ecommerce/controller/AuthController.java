package com.mitsuha754.ecommerce.controller;

import com.mitsuha754.ecommerce.result.R;
import com.mitsuha754.ecommerce.dto.LoginDTO;
import com.mitsuha754.ecommerce.dto.RegisterDTO;
import com.mitsuha754.ecommerce.util.CookieUtils;
import com.mitsuha754.ecommerce.util.LoginUtil;
import com.mitsuha754.ecommerce.vo.UserVO;
import com.mitsuha754.ecommerce.service.AuthService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;


@RestController
public class AuthController {

    @Resource
    private AuthService authService;

    @Resource
    private LoginUtil loginUtil;


    //用户登录：Web 端存 cookie，小程序端从响应体自行保存 refreshToken
    @PostMapping("/login")
    public R<UserVO> login(@RequestBody LoginDTO loginDTO,
                           HttpServletRequest request,
                           HttpServletResponse response){

        UserVO userVO = authService.login(loginDTO);

        //校验是否https请求
        boolean https = CookieUtils.isHttps(request);

        //token存活时间,-1为关闭浏览器就失效
        int refreshAge;

        //是否记住登录来判断存活时间
        if (loginDTO.getIsRemember()) {
            refreshAge = loginUtil.getRefreshExpire();
        } else {
            refreshAge = -1;
        }

        // 长期token同时写入 cookie（Web 端用）与响应体（小程序端保存用）
        CookieUtils.setCookie(response,
                "refresh_token",
                userVO.getRefreshToken(),
                refreshAge,
                https,
                "Lax");
        // 注意：小程序无 cookie，需要通过响应体拿到 refreshToken，故此处不再置空
        return R.ok(userVO);
    }

    @PostMapping("/refresh")
    public R<UserVO> refresh(HttpServletRequest request, HttpServletResponse response) {
        // 优先取请求头 refreshToken（小程序端用），其次取 cookie（Web 端用）
        String refreshToken = request.getHeader("refreshToken");
        if (!StringUtils.hasText(refreshToken)) {
            refreshToken = CookieUtils.getCookie(request, "refresh_token");
        }
        boolean https = CookieUtils.isHttps(request);
        //先判断tok的en存不存在
        if (StringUtils.hasText(refreshToken)) {
            // 业务校验失败直接返回null，不抛业务异常
            UserVO userVO = authService.refreshToken(refreshToken);
            //判断token存在但是失效的情况
            if(userVO == null){
                //token无效，清除浏览器残留cookie
                CookieUtils.removeCookie(response,"refresh_token",https);
                return R.ok();
            }

            // Web 端刷新后重新写入 cookie，小程序端通过响应体读取
            userVO.setRefreshToken(null);
            return R.ok(userVO);
        }
        // cookie本身就没有，属于正常场景，返回业务401，不抛异常，不打印异常堆栈
        return R.ok();
    }

    //用户注册
    @PostMapping("/register")
    public R<?> register(@RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return R.ok("注册成功");
    }

    @PostMapping("/logout")
    public R<?> logout(HttpServletRequest request,
                       HttpServletResponse response){
        String refreshToken = CookieUtils.getCookie(request, "refresh_token");
        // 同时判null + 空字符串
        if (refreshToken != null && !refreshToken.isEmpty()) {
            // 1. 先销毁redis中的refreshToken，让这个token作废
            authService.logout(refreshToken);
            // 2. 再清除浏览器端cookie
            boolean https = CookieUtils.isHttps(request);
            CookieUtils.removeCookie(response, "refresh_token", https);
        }
        return R.ok("退出成功");
    }

    @GetMapping("/getCaptchaCode")
    public R<Map<String,String>> getCode(){
        return R.ok(authService.getCode());
    }
}
