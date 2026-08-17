package com.mitsuha754.ecommerce.service;

import com.mitsuha754.ecommerce.entity.Category;

import java.util.List;

public interface CategoryService {
    /**
     *
     * @return 所有类别集合
     */
   List<Category> getCategoryList();

    /**
     * 添加类别
     */
    void addCategory(Category category);

    /**
     * 删除类别
     */
    void deleteCategoryById(Long id);

    /**
     * 更改类别属性
     */
    void updateCategory(Category category);
}

