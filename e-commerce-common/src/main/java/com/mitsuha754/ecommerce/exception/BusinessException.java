package com.mitsuha754.ecommerce.exception;

import lombok.Getter;

/**
 * 自定义业务异常（如“账号密码错误”“参数为空”等）
 */
@Getter
public class BusinessException extends RuntimeException {
    // getter
    private final Integer code; // 异常状态码

    public BusinessException(String message) {
        super(message);
        this.code = 400; // 默认400
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

}