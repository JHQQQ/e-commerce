package com.mitsuha754.ecommerce.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtils {


    /**
     * 构建单条Set‑Cookie字符串
     * @param name cookie名
     * @param value cookie值
     * @param maxAge 秒 -1会话，0删除
     * @param isHttps 是否https
     * @param sameSite Lax / Strict / None
     * @return 一条完整Set‑Cookie的value字符串
     */
    public static String buildSetCookieStr(String name,
                                           String value,
                                           int maxAge,
                                           boolean isHttps,
                                           String sameSite) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("=").append(value);
        sb.append(";Path=/");
        sb.append(";HttpOnly");
        if(maxAge >=0){
            sb.append(";Max-Age=").append(maxAge);
        }
        if(isHttps){
            sb.append(";Secure");
        }
        if(sameSite != null && !sameSite.isBlank()){
            sb.append(";SameSite=").append(sameSite);
        }
        return sb.toString();
    }

    /**
     * 设置Cookie，支持SameSite，多条Cookie不会互相覆盖
     * @param response HttpServletResponse
     * @param name name
     * @param value value
     * @param maxAge 秒
     * @param isHttps https?
     * @param sameSite SameSite=Lax / Strict / None
     */
    public static void setCookie(HttpServletResponse response,
                                 String name,
                                 String value,
                                 int maxAge,
                                 boolean isHttps,
                                 String sameSite) {
        // 关键点：addHeader，多次调用，添加多条Set‑Cookie
        // Tomcat 10+ 正常支持多条同名响应头
        response.addHeader("Set-Cookie", buildSetCookieStr(name, value, maxAge, isHttps, sameSite));
    }

    /**
     * 读取Cookie
     */
    public static String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null || name == null){
            return null;
        }
        for (Cookie cookie : cookies) {
            if(name.equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 删除Cookie
     */
    public static void removeCookie(HttpServletResponse response, String name, boolean isHttps){
        setCookie(response,name,"",0,isHttps,"Lax");
    }

    /**
     * 判断当前请求是否HTTPS
     */
    public static boolean isHttps(HttpServletRequest request){
        return "https".equalsIgnoreCase(request.getScheme());
    }
}