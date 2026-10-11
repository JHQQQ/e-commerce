package com.mitsuha754.ecommerce.service;

import com.mitsuha754.ecommerce.result.PageResult;
import com.mitsuha754.ecommerce.dto.MerchantProductQueryDTO;
import com.mitsuha754.ecommerce.dto.ProductQueryDTO;
import com.mitsuha754.ecommerce.entity.Product;
import com.mitsuha754.ecommerce.vo.MerchantProductVO;
import com.mitsuha754.ecommerce.vo.ProductVO;
import com.mitsuha754.ecommerce.vo.ShopPublicVO;


public interface ProductService {

    // ==================== 用户端 ====================

    /**
     * 分页条件查询商品列表（仅已上架且审核通过）
     */
    PageResult<Product> getProductPage(ProductQueryDTO queryDTO);

    /**
     * 根据ID查询商品详情（仅已上架且审核通过）
     */
    ProductVO selectProductVOById(Long id);

    /**
     * 查询商品所属店铺的公开信息（姓名已脱敏，只显示姓）
     * 用户端商品详情页「查看商家信息」使用，游客可访问
     */
    ShopPublicVO getShopByProductId(Long productId);

    // ==================== 商家端 ====================

    /**
     * 分页查询当前商家的商品（含待审核 / 已驳回）
     */
    PageResult<Product> getMerchantProductPage(MerchantProductQueryDTO queryDTO, Long merchantId);

    /**
     * 商家新增商品：提交后进入待审核状态，审核通过前不会在前台展示
     */
    void addMerchantProduct(Product product, Long merchantId);

    /**
     * 商家修改商品：修改后重新进入待审核状态并下架
     */
    void updateMerchantProduct(Product product, Long merchantId);

    /**
     * 商家删除商品（只能删除自己的）
     */
    void deleteMerchantProduct(Long id, Long merchantId);

    /**
     * 商家上架/下架自己的商品（仅审核通过的商品可操作）
     *
     * @param status 1 上架 / 0 下架
     */
    void updateMerchantProductStatus(Long id, Integer status, Long merchantId);

    // ==================== 管理端（商品审核） ====================

    /**
     * 分页查询商家商品（管理端审核列表）
     */
    PageResult<MerchantProductVO> getAuditProductPage(MerchantProductQueryDTO queryDTO);

    /**
     * 审核商家商品
     *
     * @param approved true 通过并上架 / false 驳回并下架
     */
    void auditProduct(Long id, boolean approved);

}
