package com.mitsuha754.ecommerce.controller;

import com.mitsuha754.ecommerce.annotation.AdminOnly;
import com.mitsuha754.ecommerce.dto.ProductQueryDTO;
import com.mitsuha754.ecommerce.entity.Category;
import com.mitsuha754.ecommerce.entity.OrderItem;
import com.mitsuha754.ecommerce.entity.Product;
import com.mitsuha754.ecommerce.result.PageResult;
import com.mitsuha754.ecommerce.result.R;
import com.mitsuha754.ecommerce.entity.User;
import com.mitsuha754.ecommerce.service.CategoryService;
import com.mitsuha754.ecommerce.service.ProductService;
import com.mitsuha754.ecommerce.util.OssUtil;
import com.mitsuha754.ecommerce.vo.AdminOrderListVO;
import com.mitsuha754.ecommerce.vo.UserQueryVO;
import com.mitsuha754.ecommerce.service.AdminService;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/admin")
@AdminOnly
public class AdminController {

    @Resource
    private AdminService adminService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private ProductService productService;

    @Resource
    private OssUtil ossUtil;

    @PostMapping("/user/list")
    public R<List<UserQueryVO>> getAllUsers() {
        List<UserQueryVO> list = adminService.getAllUsers();
        return R.ok(list);
    }

    @PostMapping("/category/list")
    public R<List<Category>> getCategory(){
        List<Category> category = categoryService.getCategoryList();
        return R.ok(category);
    }

    @PostMapping("/category/add")
    public R<?> addCategory(@RequestBody Category category){
        categoryService.addCategory(category);
        return R.ok(category.getName() + "添加成功");
    }

    @DeleteMapping("/category/delete")
    public R<?> deleteCategory(@RequestParam Long id){
        categoryService.deleteCategoryById(id);
        return R.ok("删除成功");
    }

    @PostMapping("/category/update")
    public R<?> updateCategory(@RequestBody Category category){
        categoryService.updateCategory(category);

        return R.ok();


    }@PostMapping("/product/add")
    public R<?> addProduct(
            @RequestParam String name,
            @RequestParam Long categoryId,
            @RequestParam BigDecimal price,
            @RequestParam Integer stock,
            @RequestParam Integer status,
            @RequestParam(required = false) MultipartFile file
    ) {
        // 1. 构建商品对象
        Product product = new Product();
        product.setName(name);
        product.setCategoryId(categoryId);
        product.setPrice(price);
        product.setStock(stock);
        product.setStatus(status);

        // 2. 上传图片
        if (file != null && !file.isEmpty()) {
            String url = ossUtil.upload(file, "product/");
            product.setImage(url);
        }

        // 3. 保存
        productService.addProduct(product);
        return R.ok();
    }

    @PostMapping("/product/update")
    public R<?> updateProduct(
            @RequestParam Long id,  // 必须传ID
            @RequestParam String name,
            @RequestParam Long categoryId,
            @RequestParam BigDecimal price,
            @RequestParam Integer stock,
            @RequestParam Integer status,
            @RequestParam(required = false) MultipartFile file
    ) {
        // 1. 构建商品对象
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setCategoryId(categoryId);
        product.setPrice(price);
        product.setStock(stock);
        product.setStatus(status);

        // 2. 上传图片
        if (file != null && !file.isEmpty()) {
            String url = ossUtil.upload(file, "product/");
            product.setImage(url);
        }

        // 3. 更新
        productService.editProduct(product);
        return R.ok();
    }

    @GetMapping("/product/list")
    public R<PageResult<Product>> getProductList(ProductQueryDTO productQueryDTO) {
        PageResult<Product> list = productService.getProductPage(productQueryDTO);
        return R.ok(list);
    }

    @PostMapping("/user/updateStatus")
    public R<Boolean> updateStatus(@RequestBody User user) {
        adminService.updateUserStatus(user.getId() , user.getStatus());
        return R.ok(true);
    }

    @PostMapping("/user/updateBalance")
    public R<Boolean> updateBalance(@RequestBody User user) {
        adminService.updateUserBalance(user.getId() , user.getBalance());
        return R.ok(true);
    }

    @PostMapping("/order/list")
    public R<List<AdminOrderListVO>> getAllOrders() {
        return R.ok(adminService.showAllOrders());
    }

    @PostMapping("/order/deliver")
    public R<?> deliverOrder(@RequestParam String orderNo) {
        adminService.toDeliver(orderNo);
        return R.ok();
    }

    @GetMapping("/order/detail/{orderNo}")
    public R<List<OrderItem>> getOrderDetail(@PathVariable String orderNo) {
        return R.ok(adminService.getOrderDetail(orderNo));
    }

    @PostMapping("/order/cancel")
    public R<?> cancelOrder(@RequestParam String orderNo) {
        adminService.cancelOrder(orderNo);
        return R.ok();
    }
}
