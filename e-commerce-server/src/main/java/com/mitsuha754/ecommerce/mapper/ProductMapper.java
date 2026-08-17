package com.mitsuha754.ecommerce.mapper;

import com.mitsuha754.ecommerce.dto.ProductQueryDTO;
import com.mitsuha754.ecommerce.entity.Product;
import com.mitsuha754.ecommerce.vo.ProductVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {

    // 分页条件查询商品列表
    List<Product> selectProductPage(@Param("query") ProductQueryDTO query);

    // 根据类别ID查询商品
    List<Product> selectProductByCategoryId(@Param("id") Long id);

    //根据商品ID查询(商品详细界面)
    ProductVO selectProductVOById(@Param("id") Long id);

    // 查询总数（分页用）
    long selectProductCount(@Param("query") ProductQueryDTO query);

    //编辑商品
    void editProduct(@Param("Product") Product product);

    //新增商品
    void addProduct(@Param("Product") Product product);


}