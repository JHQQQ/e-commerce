package com.mitsuha754.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * 跨域配置
 *
 * 允许的前端源由配置项 cors.allowed-origins 控制（逗号分隔）。
 * 默认允许本地开发常用的 localhost 端口；生产环境请通过环境变量
 * CORS_ALLOWED_ORIGINS 覆盖为实际前端域名。
 *
 * 支持两种取值：
 *  - 精确域名列表：https://shop.example.com,https://admin.example.com
 *  - 通配符 *：允许所有来源（配合 allowCredentials=false 使用）
 */
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:5173,http://localhost:3000,http://localhost:8080,http://localhost:4173}")
    private String allowedOrigins;

    // 是否允许携带凭证（cookie、authorization header）
    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        String trimmed = allowedOrigins.trim();
        boolean useWildcard = "*".equals(trimmed);

        if (useWildcard) {
            // 通配符 + 凭证：Spring 要求用 setAllowedOriginPatterns 而不是 setAllowedOrigins 才能配通配
            config.setAllowedOriginPatterns(List.of("*"));
            config.setAllowCredentials(false);
        } else {
            List<String> origins = Arrays.stream(trimmed.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            config.setAllowedOrigins(origins);
            config.setAllowCredentials(allowCredentials);
        }

        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
