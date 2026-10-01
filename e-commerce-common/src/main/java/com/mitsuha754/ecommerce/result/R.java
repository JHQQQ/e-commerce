package com.mitsuha754.ecommerce.result;

import com.mitsuha754.ecommerce.enums.ResultCodeEnum;
import lombok.Data;


/**
 * 全局统一返回结果
 * 规范：success（布尔）+ code（状态码）+ msg（提示）+ data（数据）
 */
@Data
public class R<T> {
    // 业务状态：true=成功，false=失败
    private boolean success;
    // 状态码 详情看枚举类
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
        r.setCode(ResultCodeEnum.SUCCESS.getCode());
        r.setMsg("操作成功");
        return r;
    }

    // 快速返回成功结果（带数据）
    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setSuccess(true);
        r.setCode(ResultCodeEnum.SUCCESS.getCode());
        r.setMsg("操作成功");
        r.setData(data);
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