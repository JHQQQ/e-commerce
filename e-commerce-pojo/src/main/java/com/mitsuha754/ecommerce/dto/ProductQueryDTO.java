package com.mitsuha754.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 商品列表查询 DTO
 * 前端传参：page, size, keyword, categoryId
 */
@Data
public class ProductQueryDTO {
    // 当前页码
    @Min(1)
    private Integer page;

    // 每页条数
    @Min(1)
    private Integer size;

    // 搜索关键词
    private String keyword;

    // 分类ID
    @Min(1)
    private Long categoryId;
}