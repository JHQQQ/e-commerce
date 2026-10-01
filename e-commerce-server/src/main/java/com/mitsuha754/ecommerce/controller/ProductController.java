package com.mitsuha754.ecommerce.controller;

import com.mitsuha754.ecommerce.result.PageResult;
import com.mitsuha754.ecommerce.result.R;
import com.mitsuha754.ecommerce.dto.ProductQueryDTO;
import com.mitsuha754.ecommerce.entity.Product;
import com.mitsuha754.ecommerce.vo.ProductVO;
import com.mitsuha754.ecommerce.service.ProductService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/product")
@RestController
@Validated
public class ProductController {

    @Resource
    private ProductService productService;


    @GetMapping("/list")
    public R<PageResult<Product>> getProductList(@Valid ProductQueryDTO productQueryDTO) {
        return R.ok(productService.getProductPage(productQueryDTO));
    }



    @GetMapping("/detail/{id}")
    public R<ProductVO> detailProduct(@PathVariable
                                      @NotNull(message = "商品id不能为空")
                                      @Min(value = 1, message = "商品id必须大于0")
                                      Long id) {
        return R.ok(productService.selectProductVOById(id));
    }

}
