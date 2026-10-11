package com.mitsuha754.ecommerce.interceptor;


import com.mitsuha754.ecommerce.enums.ResultCodeEnum;
import com.mitsuha754.ecommerce.exception.BusinessException;
import com.mitsuha754.ecommerce.util.LoginUtil;
import com.mitsuha754.ecommerce.vo.UserLoginVO;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import io.jsonwebtoken.Claims;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private LoginUtil loginUtil;

    @Getter
    public static ThreadLocal<UserLoginVO> USER = new ThreadLocal<>();

    /**
     * 获取当前登录用户
     * 未登录时抛出 401 业务异常，避免各接口信任前端传入的 userName 造成越权。
     */
    public static UserLoginVO requireCurrentUser() {
        UserLoginVO user = USER.get();
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED.getCode(), ResultCodeEnum.UNAUTHORIZED.getMessage());
        }
        return user;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {

        // 游客放行名单：不需要登录就能访问的接口
        String uri = request.getRequestURI();

        // 跨域预检请求（OPTIONS）直接放行：浏览器先发 OPTIONS，不能带业务 token
        // 否则跨域请求会被拦截返回 401，导致前端请求失败
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(ResultCodeEnum.SUCCESS.getCode());
            return true;
        }

        if (uri.equals("/") || uri.startsWith("/api/product/detail") || uri.startsWith("/api/product/list")
                || uri.startsWith("/api/product/shop")
                || uri.startsWith("/api/category/list") || uri.startsWith("/api/refresh") || uri.startsWith("/api/getCaptchaCode")) {
            // 首页 / 商品列表 / 商品详情 / 店铺公开信息 / 商品分类 → 游客直接放行
            return true;
        }

        //  从请求头获取 accessToken
        String accessToken = request.getHeader("accessToken");

        try {
            //  解析 accessToken
            Claims claims = loginUtil.parseToken(accessToken);

            UserLoginVO user = new UserLoginVO();
            user.setUserId(claims.get("id", Integer.class));
            user.setUserName(claims.get("userName", String.class));
            user.setRole(claims.get("role", String.class));

            USER.set(user);

        }
        catch (Exception e) {
            //accessToken出现异常
            response.setStatus(ResultCodeEnum.UNAUTHORIZED.getCode());
            return false;
        }

        return true;
    }


    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {
        USER.remove();
    }
}