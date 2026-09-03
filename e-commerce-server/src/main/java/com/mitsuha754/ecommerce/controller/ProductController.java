package com.mitsuha754.ecommerce.controller;

import com.mitsuha754.ecommerce.result.PageResult;
import com.mitsuha754.ecommerce.result.R;
import com.mitsuha754.ecommerce.dto.ProductQueryDTO;
import com.mitsuha754.ecommerce.entity.Product;
import com.mitsuha754.ecommerce.vo.ProductVO;
import com.mitsuha754.ecommerce.service.ProductService;
import com.mitsuha754.ecommerce.util.OssUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RequestMapping("/product")
@RestController
public class ProductController {

    @Resource
    private ProductService productService;


    @GetMapping("/list")
    public R<PageResult<Product>> getProductList(ProductQueryDTO productQueryDTO) {
        PageResult<Product> list = productService.getProductPage(productQueryDTO);
        return R.ok(list);
    }



    @PostMapping("/detail")
    public R<ProductVO> detailProduct(@RequestBody Map<String, Long> ids) {
        Long id =  ids.get("id");
        ProductVO productVO = productService.selectProductVOById(id);
        return R.ok(productVO);

    }

}
