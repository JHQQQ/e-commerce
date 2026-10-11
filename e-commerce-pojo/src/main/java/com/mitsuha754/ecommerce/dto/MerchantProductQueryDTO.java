package com.mitsuha754.ecommerce.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 商家商品查询 DTO（商家端商品列表 / 管理端商品审核列表共用）
 */
@Data
public class MerchantProductQueryDTO {

    /**
     * 当前页码（从 1 开始）
     */
    @Min(1)
    private Integer page;

    /**
     * 每页条数
     */
    @Min(1)
    private Integer size;

    /**
     * 商品名称关键词
     */
    private String keyword;

    /**
     * 审核状态过滤：0待审核 1已通过 2已驳回（null 表示全部）
     */
    private Integer auditStatus;

    /**
     * 商家用户ID。
     * 商家端接口忽略前端传参，由服务端强制写入当前登录用户ID，避免越权查询。
     */
    private Long merchantId;

}
