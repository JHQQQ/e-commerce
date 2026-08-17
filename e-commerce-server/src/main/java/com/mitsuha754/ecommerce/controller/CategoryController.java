package com.mitsuha754.ecommerce.controller;

import com.mitsuha754.ecommerce.result.R;
import com.mitsuha754.ecommerce.entity.Category;
import com.mitsuha754.ecommerce.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;


    @PostMapping("/list")
    public R<List<Category>> getCategory(){
        List<Category> category = categoryService.getCategoryList();
        return R.ok(category);
    }
}
