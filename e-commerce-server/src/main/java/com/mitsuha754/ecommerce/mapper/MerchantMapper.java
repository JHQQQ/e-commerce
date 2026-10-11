package com.mitsuha754.ecommerce.mapper;

import com.mitsuha754.ecommerce.entity.MerchantUpdate;
import com.mitsuha754.ecommerce.entity.Shop;
import com.mitsuha754.ecommerce.vo.MerchantUpdateVO;
import com.mitsuha754.ecommerce.vo.MerchantVO;
import com.mitsuha754.ecommerce.vo.ShopPublicVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MerchantMapper {

    /**
     * 新增商家认证申请（默认状态：审核中）
     */
    void insertMerchant(@Param("Shop") Shop shop);

    /**
     * 重新提交申请（被拒绝/被撤销后再次提交，字段覆盖并把状态重置为审核中）
     */
    void updateApplication(@Param("Shop") Shop shop);

    /**
     * 按用户ID查询商家信息（含用户名）
     */
    MerchantVO getByUserId(@Param("userId") Long userId);

    /**
     * 管理端：查询商家列表
     *
     * @param status 商家状态，null 表示查询全部
     */
    List<MerchantVO> listMerchants(@Param("status") Integer status);

    /**
     * 更新商家状态
     *
     * @return 影响行数
     */
    int updateStatus(@Param("userId") Long userId, @Param("status") Integer status);

    // ==================== 商家资料变更申请 ====================

    /**
     * 新增资料变更申请（默认状态：待审核）
     */
    void insertUpdateApply(@Param("Update") MerchantUpdate update);

    /**
     * 重新提交资料变更申请（覆盖内容并把状态重置为待审核）
     */
    int updateUpdateApply(@Param("Update") MerchantUpdate update);

    /**
     * 查询某个商家最近一次资料变更申请，没有则返回 null
     */
    MerchantUpdate selectUpdateApply(@Param("userId") Long userId);

    /**
     * 管理端：资料变更申请列表（含商家现有资料做对比）
     *
     * @param status 审核状态，null 表示全部
     */
    List<MerchantUpdateVO> listUpdateApplies(@Param("status") Integer status);

    /**
     * 更新申请审核状态
     */
    int updateApplyStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 审核通过：把申请内容写回商家资料
     */
    int applyUpdateToMerchant(@Param("userId") Long userId);

    // ==================== 公开信息 ====================

    /**
     * 按商品ID查询所属商家的公开信息（contact_name 由服务层脱敏）
     */
    ShopPublicVO selectShopByProductId(@Param("productId") Long productId);

}
