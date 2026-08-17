package com.mitsuha754.ecommerce.exception;


import com.mitsuha754.ecommerce.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器：捕获所有控制器异常，返回统一格式
 */
@Slf4j // 日志注解
@RestControllerAdvice // 全局控制器异常处理，返回JSON
public class GlobalExceptionHandler {

    // 捕获自定义业务异常
    @ExceptionHandler(BusinessException.class)
    public R<?> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    // 捕获系统异常（如空指针、数组越界等）
    @ExceptionHandler(Exception.class)
    public R<?> handleSystemException(Exception e) {
        log.error("系统异常：", e); // 打印完整堆栈，便于排查
        return R.error(500, "系统繁忙，请稍后重试"); // 隐藏系统级错误
    }
}