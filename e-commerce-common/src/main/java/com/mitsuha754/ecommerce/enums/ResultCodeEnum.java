package com.mitsuha754.ecommerce.enums;

import lombok.Getter;

/**
 * 全局返回状态码枚举
 */
@Getter
public enum ResultCodeEnum {

    // ========== 通用状态码 ==========
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    // ========== 客户端错误 4xx ==========
    PARAM_ERROR(400, "参数校验失败"),
    UNAUTHORIZED(401, "token异常"),
    NO_TOKEN(4001,"未登录"),
    FORBIDDEN(403, "没有访问权限"),
    NOT_FOUND(404, "资源不存在"),

    // ========== 登录业务相关 ==========
    USERNAME_OR_PASSWORD_ERROR(10001, "用户名或密码错误"),
    ACCOUNT_DISABLE(10002, "账号已禁用，请联系管理员"),
    ACCOUNT_LOCKED(10003, "账号已锁定，请稍后重试"),
    CAPTCHA_ERROR(10004, "验证码错误"),
    CAPTCHA_EXPIRE(10005, "验证码已过期"),

    // ========== 服务端异常 5xx ==========
    SERVER_ERROR(500, "网络异常，请稍后重试");

    /**
     * 状态码
     */
    private final Integer code;
    /**
     * 提示信息
     */
    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
