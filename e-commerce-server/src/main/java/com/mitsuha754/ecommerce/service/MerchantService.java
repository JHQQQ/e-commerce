package com.mitsuha754.ecommerce.service;

import com.mitsuha754.ecommerce.dto.ApplyMerchantDTO;
import com.mitsuha754.ecommerce.dto.MerchantUpdateDTO;
import com.mitsuha754.ecommerce.entity.MerchantUpdate;
import com.mitsuha754.ecommerce.vo.MerchantStatusVO;
import com.mitsuha754.ecommerce.vo.MerchantVO;

public interface MerchantService {

    /**
     * 查询当前登录用户的商家认证状态
     */
    MerchantStatusVO getStatus();

    /**
     * 提交商家认证申请
     * 说明：一个用户只有一条商家记录；被拒绝/撤销后可重新提交，状态重置为审核中
     */
    void applyMerchant(ApplyMerchantDTO dto);

    /**
     * 获取当前登录商家的店铺信息（必须是已通过审核的商家）
     */
    MerchantVO getCurrentMerchant();

    /**
     * 校验当前登录用户是否为「已通过审核的商家」，并返回其用户ID
     * 供商家端商品/订单/资料变更接口复用，防止利用过期 token 越权操作
     */
    Long requireApprovedMerchantId();

    // ==================== 店铺资料变更（需管理员审核） ====================

    /**
     * 提交店铺资料变更申请：不直接修改商家资料，管理员审核通过后才生效
     */
    void applyUpdate(MerchantUpdateDTO dto);

    /**
     * 查询自己最近一次资料变更申请（含审核状态），没有则返回 null
     */
    MerchantUpdate getMyUpdateApply();

}
