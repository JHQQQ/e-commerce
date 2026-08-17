package com.mitsuha754.ecommerce.mapper;

import com.mitsuha754.ecommerce.entity.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryMapper {

    /**
     * @return 商品类别列表
    */
    List<Category> getCategoryList();

    /**
     * 添加类别
     */
    void addCategory(@Param("Category") Category category);

    /**
     * 删除类别
     */
    void deleteCategoryById(@Param("id") Long id);

    /**
     * 更改类别
     */
    void updateCategory(@Param("Category")Category category);

    /**
     * 根据类别ID查类名
     */
    String selectCategoryNameById(@Param("id") Long id);

}
