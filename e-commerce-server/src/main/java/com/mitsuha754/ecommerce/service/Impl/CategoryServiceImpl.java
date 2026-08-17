package com.mitsuha754.ecommerce.service.Impl;

import com.mitsuha754.ecommerce.exception.BusinessException;
import com.mitsuha754.ecommerce.mapper.CategoryMapper;
import com.mitsuha754.ecommerce.mapper.ProductMapper;
import com.mitsuha754.ecommerce.entity.Category;
import com.mitsuha754.ecommerce.entity.Product;
import com.mitsuha754.ecommerce.service.CategoryService;
import jakarta.annotation.Resource;
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
        categoryMapper.addCategory(category);
    }

    @Override
    public void deleteCategoryById(Long id) {
        List<Product> productList = productMapper.selectProductByCategoryId(id);

        if(!productList.isEmpty()){
            throw new BusinessException("该类别商品数量大于0,不允许删除");
        }
        categoryMapper.deleteCategoryById(id);
    }

    @Override
    public void updateCategory(Category category) {
        categoryMapper.updateCategory(category);
    }
}
