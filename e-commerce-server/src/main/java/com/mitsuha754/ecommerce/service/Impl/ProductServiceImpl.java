package com.mitsuha754.ecommerce.service.Impl;

import com.mitsuha754.ecommerce.exception.BusinessException;
import com.mitsuha754.ecommerce.result.PageResult;
import com.mitsuha754.ecommerce.mapper.CategoryMapper;
import com.mitsuha754.ecommerce.mapper.MerchantMapper;
import com.mitsuha754.ecommerce.mapper.ProductMapper;
import com.mitsuha754.ecommerce.dto.MerchantProductQueryDTO;
import com.mitsuha754.ecommerce.dto.ProductQueryDTO;
import com.mitsuha754.ecommerce.entity.Product;
import com.mitsuha754.ecommerce.vo.MerchantProductVO;
import com.mitsuha754.ecommerce.vo.ProductVO;
import com.mitsuha754.ecommerce.vo.ShopPublicVO;
import com.mitsuha754.ecommerce.service.ProductService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    /** 审核状态：待审核 */
    private static final int AUDIT_PENDING = 0;
    /** 审核状态：已通过 */
    private static final int AUDIT_APPROVED = 1;
    /** 审核状态：已驳回 */
    private static final int AUDIT_REJECTED = 2;

    /** 上下架状态：下架 */
    private static final int OFF_SHELF = 0;
    /** 上下架状态：上架 */
    private static final int ON_SHELF = 1;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private MerchantMapper merchantMapper;

    // ==================== 用户端 ====================

    /**
     * 分页 / 分类 / 搜索 商品查询（仅已上架且审核通过）
     */
    @Override
    public PageResult<Product> getProductPage(ProductQueryDTO queryDTO) {

        if (queryDTO.getPage() == null || queryDTO.getSize() == null) {
            List<Product> list = productMapper.selectProductPage(queryDTO);
            return new PageResult<>(list.size(), list);
        }

        // 分页计算：把页码换算成 LIMIT 的偏移量
        int offset = (queryDTO.getPage() - 1) * queryDTO.getSize();
        queryDTO.setPage(offset);

        List<Product> productList = productMapper.selectProductPage(queryDTO);
        long total = productMapper.selectProductCount(queryDTO);

        return new PageResult<>(total, productList);
    }

    @Override
    public ProductVO selectProductVOById(Long id) {
        ProductVO productVO = productMapper.selectProductVOById(id);

        if (productVO == null) {
            throw new BusinessException("商品不存在或已下架");
        }

        return productVO;
    }

    @Override
    public ShopPublicVO getShopByProductId(Long productId) {
        if (productId == null) {
            throw new BusinessException("商品ID不能为空");
        }
        ShopPublicVO shop = merchantMapper.selectShopByProductId(productId);
        if (shop == null) {
            throw new BusinessException("该商品暂无店铺信息");
        }
        // 联系人姓名脱敏：只保留姓氏，其余用 * 代替
        shop.setContactName(maskContactName(shop.getContactName()));
        return shop;
    }

    /**
     * 姓名脱敏：只显示姓（张三 → 张*，欧阳娜娜 → 欧***）。
     * 少于等于一个字时原样返回；没有姓名时展示「商家」。
     */
    private String maskContactName(String name) {
        if (!StringUtils.hasText(name)) {
            return "商家";
        }
        String trimmed = name.trim();
        if (trimmed.length() <= 1) {
            return trimmed;
        }
        return trimmed.charAt(0) + "*".repeat(trimmed.length() - 1);
    }

    // ==================== 商家端 ====================

    @Override
    public PageResult<Product> getMerchantProductPage(MerchantProductQueryDTO queryDTO, Long merchantId) {
        // 强制使用当前登录商家的ID，防止越权查询别人的商品
        queryDTO.setMerchantId(merchantId);

        if (queryDTO.getPage() == null || queryDTO.getSize() == null) {
            List<Product> list = productMapper.selectMerchantProductPage(queryDTO);
            return new PageResult<>(list.size(), list);
        }

        int offset = (queryDTO.getPage() - 1) * queryDTO.getSize();
        queryDTO.setPage(offset);

        List<Product> list = productMapper.selectMerchantProductPage(queryDTO);
        long total = productMapper.selectMerchantProductCount(queryDTO);
        return new PageResult<>(total, list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMerchantProduct(Product product, Long merchantId) {
        checkProductParams(product);
        checkCategory(product.getCategoryId());

        // 商家提交的商品一律进入待审核 + 下架状态
        product.setId(null);
        product.setMerchantId(merchantId);
        product.setStatus(OFF_SHELF);
        product.setAuditStatus(AUDIT_PENDING);

        productMapper.addProduct(product);
        log.info("商家[{}]提交商品[{}]待审核", merchantId, product.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMerchantProduct(Product product, Long merchantId) {
        if (product.getId() == null) {
            throw new BusinessException("商品ID不能为空");
        }
        requireOwnProduct(product.getId(), merchantId);
        checkProductParams(product);
        checkCategory(product.getCategoryId());

        // 修改后重新走一遍审核流程
        product.setMerchantId(merchantId);
        // 图片传空串表示「删除图片」，统一转成 null 落库
        if (!StringUtils.hasText(product.getImage())) {
            product.setImage(null);
        }

        int rows = productMapper.updateMerchantProduct(product);
        if (rows == 0) {
            throw new BusinessException("商品修改失败，请刷新后重试");
        }
        log.info("商家[{}]修改商品[{}]，重新进入待审核", merchantId, product.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMerchantProduct(Long id, Long merchantId) {
        if (id == null) {
            throw new BusinessException("商品ID不能为空");
        }
        requireOwnProduct(id, merchantId);

        int rows = productMapper.deleteMerchantProduct(id, merchantId);
        if (rows == 0) {
            throw new BusinessException("商品删除失败，请刷新后重试");
        }
        log.info("商家[{}]删除商品[{}]", merchantId, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMerchantProductStatus(Long id, Integer status, Long merchantId) {
        if (id == null || status == null || (status != OFF_SHELF && status != ON_SHELF)) {
            throw new BusinessException("参数异常");
        }

        Product exist = requireOwnProduct(id, merchantId);

        // 只有审核通过的商品才允许上架，避免绕过审核
        if (status == ON_SHELF && !Objects.equals(exist.getAuditStatus(), AUDIT_APPROVED)) {
            throw new BusinessException("商品尚未通过审核，暂时不能上架");
        }

        int rows = productMapper.updateProductStatus(id, merchantId, status);
        if (rows == 0) {
            throw new BusinessException("操作失败，商品状态可能已变更");
        }
        log.info("商家[{}]{}商品[{}]", merchantId, status == ON_SHELF ? "上架" : "下架", id);
    }

    // ==================== 管理端（商品审核） ====================

    @Override
    public PageResult<MerchantProductVO> getAuditProductPage(MerchantProductQueryDTO queryDTO) {
        if (queryDTO.getPage() == null || queryDTO.getSize() == null) {
            List<MerchantProductVO> list = productMapper.selectAuditProductPage(queryDTO);
            return new PageResult<>(list.size(), list);
        }

        int offset = (queryDTO.getPage() - 1) * queryDTO.getSize();
        queryDTO.setPage(offset);

        List<MerchantProductVO> list = productMapper.selectAuditProductPage(queryDTO);
        long total = productMapper.selectAuditProductCount(queryDTO);
        return new PageResult<>(total, list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditProduct(Long id, boolean approved) {
        if (id == null) {
            throw new BusinessException("商品ID不能为空");
        }

        Product product = productMapper.selectProductById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (!Objects.equals(product.getAuditStatus(), AUDIT_PENDING)) {
            throw new BusinessException("该商品不在待审核状态");
        }

        int auditStatus = approved ? AUDIT_APPROVED : AUDIT_REJECTED;
        int shelfStatus = approved ? ON_SHELF : OFF_SHELF;

        productMapper.updateAuditStatus(id, auditStatus, shelfStatus);
        log.info("商品[{}]审核{}", id, approved ? "通过并上架" : "驳回并下架");
    }

    // ==================== 私有校验方法 ====================

    /**
     * 校验商品基础参数
     */
    private void checkProductParams(Product product) {
        if (!StringUtils.hasText(product.getName())) {
            throw new BusinessException("商品名称不能为空");
        }
        if (product.getName().length() > 200) {
            throw new BusinessException("商品名称过长");
        }
        if (product.getCategoryId() == null) {
            throw new BusinessException("请选择商品分类");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("商品价格必须大于0");
        }
        if (product.getStock() == null || product.getStock() < 0) {
            throw new BusinessException("商品库存不能为负数");
        }
    }

    /**
     * 校验分类是否存在且未被删除
     */
    private void checkCategory(Long categoryId) {
        if (categoryMapper.countEnabledCategoryById(categoryId) == 0) {
            throw new BusinessException("所选分类不存在或已删除");
        }
    }

    /**
     * 校验商品是否存在且属于当前商家
     */
    private Product requireOwnProduct(Long id, Long merchantId) {
        Product exist = productMapper.selectProductById(id);
        if (exist == null) {
            throw new BusinessException("商品不存在");
        }
        if (!Objects.equals(exist.getMerchantId(), merchantId)) {
            throw new BusinessException("无权操作他人的商品");
        }
        return exist;
    }

}
