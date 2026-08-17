package com.mitsuha754.ecommerce.aop;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mitsuha754.ecommerce.interceptor.LoginInterceptor;

import com.mitsuha754.ecommerce.result.R;
import com.mitsuha754.ecommerce.vo.UserVO;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

@Aspect
@Component
public class AdminOnlyAspect {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 切点：类上标注@AdminOnly 或者 方法上标注@AdminOnly
     */
    @Pointcut("@within(com.mitsuha754.ecommerce.annotation.AdminOnly) || @annotation(com.mitsuha754.ecommerce.annotation.AdminOnly)")
    public void pointCut() {
    }


    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = attributes != null ? attributes.getResponse() : null;

        // 未登录（拦截器已校验，但此处做兜底，避免 NPE）
        UserVO userVO = LoginInterceptor.USER.get();
        if (userVO == null) {
            writeError(response, 401, "未登录或登录已过期");
            return null;
        }

        // 非管理员 → 403
        if (!"ADMIN".equals(userVO.getRole())) {
            writeError(response, 403, "无权限访问");
            return null;
        }

        // 是管理员，放行执行业务
        return joinPoint.proceed();
    }

    /**
     * 以统一 JSON 格式写回错误响应
     */
    private void writeError(HttpServletResponse response, int status, String msg) throws IOException {
        if (response == null) {
            return;
        }
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(status);
        response.getWriter().write(MAPPER.writeValueAsString(R.error(status, msg)));
    }
}
