package com.mitsuha754.ecommerce.service.Impl;

import com.mitsuha754.ecommerce.exception.BusinessException;
import com.mitsuha754.ecommerce.mapper.CategoryMapper;
import com.mitsuha754.ecommerce.mapper.ProductMapper;
import com.mitsuha754.ecommerce.entity.Category;
import com.mitsuha754.ecommerce.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private ProductMapper productMapper;

    @Override
    public List<Category> getCategoryList() {
        return categoryMapper.getCategoryList();
    }

    @Override
    public void addCategory(Category category) {
        try {
            categoryMapper.addCategory(category);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("分类名称已存在");
        }
    }

    @Override
    public void deleteCategoryById(Long id) {
        // 任何状态（含待审核/已下架）的商品都不允许分类被删除，避免商品丢失分类
        long productCount = productMapper.countByCategoryId(id);

        if (productCount > 0) {
            throw new BusinessException("该分类下还有商品，不允许删除");
        }
        categoryMapper.deleteCategoryById(id);
    }

    @Override
    public void updateCategory(Category category) {
        try {
            categoryMapper.updateCategory(category);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("分类名称已存在");
        }
    }
}
