package com.mitsuha754.ecommerce.dto;

import lombok.Data;

/**
 * 商品列表查询 DTO
 * 前端传参：page, size, keyword, categoryId
 */
@Data
public class ProductQueryDTO {
    // 当前页码
    private Integer page;

    // 每页条数
    private Integer size;

    // 搜索关键词
    private String keyword;

    // 分类ID
    private Long categoryId;
}