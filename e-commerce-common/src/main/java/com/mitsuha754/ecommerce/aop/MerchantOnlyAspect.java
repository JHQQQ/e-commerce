package com.mitsuha754.ecommerce.aop;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mitsuha754.ecommerce.interceptor.LoginInterceptor;

import com.mitsuha754.ecommerce.result.R;
import com.mitsuha754.ecommerce.vo.UserLoginVO;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

/**
 * 商家权限切面：非商家（角色不是 MERCHANT）访问商家端接口时返回 403
 */
@Aspect
@Component
public class MerchantOnlyAspect {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 切点：类上标注@MerchantOnly 或者 方法上标注@MerchantOnly
     */
    @Pointcut("@within(com.mitsuha754.ecommerce.annotation.MerchantOnly) || @annotation(com.mitsuha754.ecommerce.annotation.MerchantOnly)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = attributes != null ? attributes.getResponse() : null;

        // 未登录（拦截器已校验，但此处做兜底，避免 NPE）
        UserLoginVO userLoginVO = LoginInterceptor.USER.get();
        if (userLoginVO == null) {
            writeError(response, 401, "未登录或登录已过期");
            return null;
        }

        // 非商家 → 403
        if (!"MERCHANT".equals(userLoginVO.getRole())) {
            writeError(response, 403, "您还不是商家，暂无权限访问商家端");
            return null;
        }

        // 是商家，放行执行业务
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
