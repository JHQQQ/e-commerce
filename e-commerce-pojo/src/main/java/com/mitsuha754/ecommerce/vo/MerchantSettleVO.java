package com.mitsuha754.ecommerce.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单结算明细 VO：某个订单需要打给某个商家的金额
 * 一个订单可能包含多个商家的商品，按 商品 → 商家 汇总后分别入账
 */
@Data
public class MerchantSettleVO {

    /**
     * 商家用户ID（product.merchant_id）
     */
    private Long merchantId;

    /**
     * 应结算金额（该商家在本订单中所有商品的小计之和）
     */
    private BigDecimal amount;

}
