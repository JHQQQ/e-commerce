package com.mitsuha754.ecommerce.controller;

import com.mitsuha754.ecommerce.dto.LoginInnerDTO;
import com.mitsuha754.ecommerce.enums.ResultCodeEnum;

import com.mitsuha754.ecommerce.result.R;
import com.mitsuha754.ecommerce.dto.LoginDTO;
import com.mitsuha754.ecommerce.dto.RegisterDTO;
import com.mitsuha754.ecommerce.util.CookieUtils;
import com.mitsuha754.ecommerce.util.LoginUtil;
import com.mitsuha754.ecommerce.vo.UserLoginVO;
import com.mitsuha754.ecommerce.service.AuthService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.Valid;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;



@RestController
public class AuthController {

    @Resource
    private AuthService authService;

    @Resource
    private LoginUtil loginUtil;


    //用户登录：Web 端存 cookie，小程序端从响应体自行保存 refreshToken
    @PostMapping("/login")
    public R<UserLoginVO> login(@RequestBody @Valid LoginDTO loginDTO,
                                HttpServletRequest request,
                                HttpServletResponse response){

        LoginInnerDTO loginInnerDTO = authService.login(loginDTO);

        //校验是否https请求
        boolean https = CookieUtils.isHttps(request);

        //token存活时间
        int refreshAge = loginUtil.getRefreshExpire();

        //是否记住登录来判断存活时间,不记住就存一天(30天/30)
        if (!loginDTO.getIsRemember()) {
            refreshAge /= 30;
        }

        // 长期token同时写入 cookie（Web 端用）
        CookieUtils.setCookie(response,
                "refresh_token",
                loginInnerDTO.getRefreshToken(),
                refreshAge,
                https,
                "Lax");

        return R.ok(loginInnerDTO.getUserLoginVO());
    }

    @PostMapping("/refresh")
    public R<UserLoginVO> refresh(HttpServletRequest request, HttpServletResponse response) {
        // 优先取请求头 refreshToken，其次取 cookie（Web 端用）
        String refreshToken = request.getHeader("refreshToken");
        if (!StringUtils.hasText(refreshToken)) {
            refreshToken = CookieUtils.getCookie(request, "refresh_token");
        }
        boolean https = CookieUtils.isHttps(request);
        //先判断token存不存在
        if (StringUtils.hasText(refreshToken)) {
            // 业务校验失败直接返回null，不抛业务异常
            LoginInnerDTO loginInnerDTO = authService.refreshToken(refreshToken);
            //判断token存在但是失效的情况
            if(loginInnerDTO.getUserLoginVO() == null){
                //token无效，清除浏览器残留cookie
                CookieUtils.removeCookie(response,"refresh_token",https);
                return R.error(ResultCodeEnum.NO_TOKEN.getCode(),"登录已失效，请重新登录!");
            }

            return R.ok(loginInnerDTO.getUserLoginVO());
        }
        // cookie本身就没有，返回4001错误码
        return R.error(ResultCodeEnum.NO_TOKEN.getCode(),"登录已失效，请重新登录!");
    }

    //用户注册
    @PostMapping("/register")
    public R<?> register(@RequestBody @Valid RegisterDTO registerDTO) {
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

    @GetMapping("/getCaptchaCode/{verify}")
    public R<Map<String,String>> getCode(@PathVariable String verify){
        return R.ok(authService.getCode(verify));
    }
}
