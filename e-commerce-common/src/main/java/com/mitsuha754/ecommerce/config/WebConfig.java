package com.mitsuha754.ecommerce.config;

import com.mitsuha754.ecommerce.interceptor.LoginInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * SpringMVC 配置类
 * 作用：注册拦截器、配置跨域、视图解析器等
 */
@Configuration // 声明这是一个配置类（Spring启动时会自动加载）
public class WebConfig implements WebMvcConfigurer {

    // 注入我们写好的 登录拦截器
    @Resource
    private LoginInterceptor loginInterceptor;

    /**
     * 重写：添加拦截器
     * 作用：告诉SpringBoot，哪些接口需要拦截、哪些不需要拦截
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor) // 注册登录拦截器

                .addPathPatterns("/**") // 拦截所有请求（所有接口都会走登录校验）

                .excludePathPatterns(
                        "/login",   // 放行：登录接口
                        "/register", // 放行：注册接口
                        "/home",
                        "/product"
                );
        // 你以后如果有不需要登录的接口，都可以加在这里
    }
}