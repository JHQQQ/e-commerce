package com.mitsuha754.ecommerce.dto;

import lombok.Data;

@Data
public class OrderDTO {
    private String userName;
    private String address;
    private String remark;
    // 立即购买：商品ID（为空则走购物车结算）
    private Long productId;
    // 立即购买：购买数量
    private Integer quantity;
    // 幂等键：前端生成，用于防止重复提交生成多个订单
    private String requestId;
}
