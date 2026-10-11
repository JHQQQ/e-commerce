package com.mitsuha754.ecommerce.mapper;

import com.mitsuha754.ecommerce.dto.MerchantProductQueryDTO;
import com.mitsuha754.ecommerce.dto.ProductQueryDTO;
import com.mitsuha754.ecommerce.entity.Product;
import com.mitsuha754.ecommerce.vo.MerchantProductVO;
import com.mitsuha754.ecommerce.vo.ProductVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {

    // ==================== 用户端 ====================

    /**
     * 分页条件查询商品列表（仅已上架且审核通过）
     */
    List<Product> selectProductPage(@Param("query") ProductQueryDTO query);

    /**
     * 查询总数（分页用，仅已上架且审核通过）
     */
    long selectProductCount(@Param("query") ProductQueryDTO query);

    /**
     * 根据商品ID查询（商品详细界面，仅已上架且审核通过）
     */
    ProductVO selectProductVOById(@Param("id") Long id);

    /**
     * 统计某分类下的商品数量（任何状态，用于分类删除校验）
     */
    long countByCategoryId(@Param("id") Long id);

    // ==================== 商家端 ====================

    /**
     * 分页查询某个商家的商品（含待审核 / 已驳回）
     */
    List<Product> selectMerchantProductPage(@Param("query") MerchantProductQueryDTO query);

    /**
     * 统计某个商家的商品总数
     */
    long selectMerchantProductCount(@Param("query") MerchantProductQueryDTO query);

    /**
     * 按ID查询商品原始数据（用于归属校验）
     */
    Product selectProductById(@Param("id") Long id);

    /**
     * 商家新增商品（merchantId / audit_status / status 由服务端强制赋值）
     */
    void addProduct(@Param("Product") Product product);

    /**
     * 商家修改商品：只允许改自己的商品，修改后重新进入待审核并下架
     */
    int updateMerchantProduct(@Param("Product") Product product);

    /**
     * 商家删除自己的商品
     */
    int deleteMerchantProduct(@Param("id") Long id, @Param("merchantId") Long merchantId);

    /**
     * 商家上架/下架（仅审核通过的商品可操作）
     */
    int updateProductStatus(@Param("id") Long id,
                            @Param("merchantId") Long merchantId,
                            @Param("status") Integer status);

    // ==================== 管理端（商品审核） ====================

    /**
     * 分页查询需要审核的商家商品（带店铺、分类信息）
     */
    List<MerchantProductVO> selectAuditProductPage(@Param("query") MerchantProductQueryDTO query);

    /**
     * 统计待审核商品总数
     */
    long selectAuditProductCount(@Param("query") MerchantProductQueryDTO query);

    /**
     * 审核商品：通过时上架，驳回时下架
     */
    int updateAuditStatus(@Param("id") Long id,
                          @Param("auditStatus") Integer auditStatus,
                          @Param("status") Integer status);

    /**
     * 撤销商家权限时，下架该商家的全部商品
     */
    int updateMerchantProductOffShelf(@Param("merchantId") Long merchantId);

}
