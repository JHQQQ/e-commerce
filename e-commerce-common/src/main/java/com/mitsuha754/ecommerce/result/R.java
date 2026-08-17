package com.mitsuha754.ecommerce.result;

import lombok.Data;

/**
 * 全局统一返回结果
 * 规范：success（布尔）+ code（状态码）+ msg（提示）+ data（数据）
 */
@Data
public class R<T> {
    // 业务状态：true=成功，false=失败
    private boolean success;
    // 状态码：200=成功，400=参数错误，401=未授权，500=系统错误
    private Integer code;
    // 提示信息
    private String msg;
    // 响应数据（泛型适配任意类型）
    private T data;

    // 私有构造器，禁止外部直接创建
    private R() {}

    // 快速返回成功结果（无数据）
    public static <T> R<T> ok() {
        R<T> r = new R<>();
        r.setSuccess(true);
        r.setCode(200);
        r.setMsg("操作成功");
        return r;
    }

    // 快速返回成功结果（带数据）
    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setSuccess(true);
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }


    // 快速返回失败结果（带提示）
    public static <T> R<T> error(String msg) {
        R<T> r = new R<>();
        r.setSuccess(false);
        r.setCode(400);
        r.setMsg(msg);
        return r;
    }

    // 快速返回失败结果（自定义状态码+提示）
    public static <T> R<T> error(Integer code, String msg) {
        R<T> r = new R<>();
        r.setSuccess(false);
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

}