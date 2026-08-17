package com.mitsuha754.ecommerce.service.Impl;

import com.mitsuha754.ecommerce.exception.BusinessException;
import com.mitsuha754.ecommerce.result.PageResult;
import com.mitsuha754.ecommerce.mapper.CategoryMapper;
import com.mitsuha754.ecommerce.mapper.ProductMapper;
import com.mitsuha754.ecommerce.dto.ProductQueryDTO;
import com.mitsuha754.ecommerce.entity.Product;
import com.mitsuha754.ecommerce.vo.ProductVO;
import com.mitsuha754.ecommerce.service.ProductService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;
    @Resource
    private CategoryMapper categoryMapper;

    /**
     * 分页 / 分类 / 搜索 商品查询（最终版）
     */
    @Override
    public PageResult<Product> getProductPage(ProductQueryDTO queryDTO) {


        if (queryDTO.getPage() == null || queryDTO.getSize() == null) {
            List<Product> list = productMapper.selectProductPage(queryDTO);
            return new PageResult<>(list.size(), list);
        }

        // 分页计算
        int pageNum = (queryDTO.getPage() - 1) * queryDTO.getSize();
        queryDTO.setPage(pageNum);

        // 查询列表
        List<Product> productList = productMapper.selectProductPage(queryDTO);

        // 总数
        long total = productMapper.selectProductCount(queryDTO);

        return new PageResult<>(total, productList);
    }

    @Override
    public void editProduct(Product product){
        if(product.getPrice().compareTo(new BigDecimal(0)) <= 0 || product.getStock() < 0){
            throw new BusinessException("参数异常");
        }
        productMapper.editProduct(product);
        log.info("商品被编辑信息:{}",product);
    }

    @Override
    public void addProduct(Product product) {
        if(product.getPrice() == null || product.getPrice().compareTo(new BigDecimal(0)) <= 0 || product.getStock() < 0){
            throw new BusinessException("参数异常");
        }
        productMapper.addProduct(product);
        log.info("商品被创建信息:{}",product);
    }

    @Override
    public ProductVO selectProductVOById(Long id) {
        ProductVO productVO = productMapper.selectProductVOById(id);
        productVO.setCategoryName(categoryMapper.selectCategoryNameById(productVO.getCategoryId()));
        return productVO;
    }


}