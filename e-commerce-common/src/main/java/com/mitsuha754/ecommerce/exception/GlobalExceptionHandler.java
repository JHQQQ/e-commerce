package com.mitsuha754.ecommerce.exception;

import com.mitsuha754.ecommerce.enums.ResultCodeEnum;
import com.mitsuha754.ecommerce.result.R;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 全局异常处理器：捕获所有控制器异常，返回统一格式
 */
@Slf4j // 日志注解
@RestControllerAdvice // 全局控制器异常处理，返回JSON
public class GlobalExceptionHandler {

    // 捕获自定义业务异常
    @ExceptionHandler(BusinessException.class)
    public R<?> handleBusinessException(BusinessException e) {
        log.info("业务异常：{}", e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    // 捕获系统异常
    @ExceptionHandler(Exception.class)
    public R<?> handleSystemException(Exception e) {
        log.error("系统异常：", e);
        return R.error(ResultCodeEnum.SERVER_ERROR.getCode(),
                ResultCodeEnum.SERVER_ERROR.getMessage()); // 系统级错误
    }

    //捕获传参异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<?> handleValidationExceptions(MethodArgumentNotValidException e) {
        String msg = Objects.requireNonNull(
                        e.getBindingResult().
                        getFieldError()).
                        getDefaultMessage();
        log.error("JSON请求参数校验失败", e);
        return R.error(ResultCodeEnum.PARAM_ERROR.getCode(), msg != null ?
                msg : ResultCodeEnum.PARAM_ERROR.getMessage());
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public R<?> handleValidationExceptions(HandlerMethodValidationException e) {
        String msg = e.getMessage();
        log.error("请求参数校验失败", e);
        return R.error(ResultCodeEnum.PARAM_ERROR.getCode(), !msg.isEmpty() ?
                msg : ResultCodeEnum.PARAM_ERROR.getMessage());
    }

    // 基础类型参数校验
    @ExceptionHandler(ConstraintViolationException.class)
    public R<?> handleConstraintViolation(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(";"));
        return R.error(ResultCodeEnum.PARAM_ERROR.getCode(), msg);
    }

    //访问不存在的接口
    @ExceptionHandler(NoHandlerFoundException.class)
    public R<?> handleNoHandlerFound(NoHandlerFoundException e){
        return R.error(ResultCodeEnum.NOT_FOUND.getCode(), "你想找啥嘞=-=");
    }
}