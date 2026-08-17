package com.mitsuha754.ecommerce.service;

import com.mitsuha754.ecommerce.result.PageResult;
import com.mitsuha754.ecommerce.dto.ProductQueryDTO;
import com.mitsuha754.ecommerce.entity.Product;
import com.mitsuha754.ecommerce.vo.ProductVO;


public interface ProductService {

    /**
     * 分页条件查询商品列表
     */
    PageResult<Product> getProductPage(ProductQueryDTO queryDTO);


    /**
     * 更改商品信息
     */
    void editProduct(Product product);

    /**
     * 新增商品
     */
    void addProduct(Product product);

    /**
     * 根据ID查询商品
     */
    ProductVO selectProductVOById(Long id);



}