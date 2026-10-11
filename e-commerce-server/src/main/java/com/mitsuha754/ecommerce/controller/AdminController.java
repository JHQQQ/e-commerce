package com.mitsuha754.ecommerce.controller;

import com.mitsuha754.ecommerce.annotation.AdminOnly;
import com.mitsuha754.ecommerce.dto.MerchantProductQueryDTO;
import com.mitsuha754.ecommerce.entity.Category;
import com.mitsuha754.ecommerce.entity.OrderItem;
import com.mitsuha754.ecommerce.entity.User;
import com.mitsuha754.ecommerce.result.PageResult;
import com.mitsuha754.ecommerce.result.R;
import com.mitsuha754.ecommerce.service.AdminService;
import com.mitsuha754.ecommerce.service.CategoryService;
import com.mitsuha754.ecommerce.service.ProductService;
import com.mitsuha754.ecommerce.vo.AdminOrderListVO;
import com.mitsuha754.ecommerce.vo.MerchantProductVO;
import com.mitsuha754.ecommerce.vo.MerchantUpdateVO;
import com.mitsuha754.ecommerce.vo.MerchantVO;
import com.mitsuha754.ecommerce.vo.UserQueryVO;

import jakarta.annotation.Resource;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端接口
 * 说明：商品由商家自行创建（管理端不再提供创建/编辑商品功能），
 *      管理员只负责分类维护、商家审核与商品上架审核。
 */
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

    // ==================== 用户管理 ====================

    @PostMapping("/user/list")
    public R<List<UserQueryVO>> getAllUsers() {
        List<UserQueryVO> list = adminService.getAllUsers();
        return R.ok(list);
    }

    @PostMapping("/user/updateStatus")
    public R<Boolean> updateStatus(@RequestBody User user) {
        adminService.updateUserStatus(user.getId(), user.getStatus());
        return R.ok(true);
    }

    @PostMapping("/user/updateBalance")
    public R<Boolean> updateBalance(@RequestBody User user) {
        adminService.updateUserBalance(user.getId(), user.getBalance());
        return R.ok(true);
    }

    // ==================== 分类管理（管理端唯一可维护的商品基础数据） ====================

    @PostMapping("/category/list")
    public R<List<Category>> getCategory() {
        List<Category> category = categoryService.getCategoryList();
        return R.ok(category);
    }

    @PostMapping("/category/add")
    public R<?> addCategory(@RequestBody Category category) {
        categoryService.addCategory(category);
        return R.ok(category.getName() + "添加成功");
    }

    @DeleteMapping("/category/delete")
    public R<?> deleteCategory(@RequestParam Long id) {
        categoryService.deleteCategoryById(id);
        return R.ok("删除成功");
    }

    @PostMapping("/category/update")
    public R<?> updateCategory(@RequestBody Category category) {
        categoryService.updateCategory(category);
        return R.ok();
    }

    // ==================== 订单管理 ====================

    /**
     * 订单列表
     *
     * @param status 订单状态过滤：0待付款 1待发货 2已发货 3已完成 4已取消；
     *               不传默认只返回进行中（待发货/已发货）
     */
    @PostMapping("/order/list")
    public R<List<AdminOrderListVO>> getAllOrders(@RequestParam(required = false) Integer status) {
        return R.ok(adminService.showAllOrders(status));
    }

    @PostMapping("/order/deliver")
    public R<?> deliverOrder(@RequestParam @NotEmpty String orderNo) {
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

    // ==================== 商家管理 ====================

    /**
     * 商家列表
     *
     * @param status 商家状态过滤：0审核中 1已通过 2已拒绝 3已撤销，不传查全部
     */
    @PostMapping("/merchant/list")
    public R<List<MerchantVO>> getMerchantList(@RequestParam(required = false) Integer status) {
        return R.ok(adminService.getMerchantList(status));
    }

    /**
     * 商家认证审核通过：升级为商家角色
     */
    @PostMapping("/merchant/approve")
    public R<?> approveMerchant(@RequestParam Long userId) {
        adminService.approveMerchant(userId);
        return R.ok("已通过该商家的认证申请");
    }

    /**
     * 商家认证审核拒绝
     */
    @PostMapping("/merchant/reject")
    public R<?> rejectMerchant(@RequestParam Long userId) {
        adminService.rejectMerchant(userId);
        return R.ok("已拒绝该商家的认证申请");
    }

    /**
     * 撤销已有商家的权限（同时下架其全部商品）
     */
    @PostMapping("/merchant/revoke")
    public R<?> revokeMerchant(@RequestParam Long userId) {
        adminService.revokeMerchant(userId);
        return R.ok("已撤销该商家的权限，其商品已全部下架");
    }

    // ==================== 商家资料变更审核 ====================

    /**
     * 商家资料变更申请列表（含现有资料与申请资料，便于对比）
     *
     * @param status 审核状态过滤：0待审核 1已通过 2已驳回，不传查全部
     */
    @PostMapping("/merchant/update/list")
    public R<List<MerchantUpdateVO>> getMerchantUpdateList(@RequestParam(required = false) Integer status) {
        return R.ok(adminService.getMerchantUpdateList(status));
    }

    /**
     * 资料变更审核通过：把申请内容写回商家资料
     */
    @PostMapping("/merchant/update/approve")
    public R<?> approveMerchantUpdate(@RequestParam Long userId) {
        adminService.approveMerchantUpdate(userId);
        return R.ok("已通过，商家资料已更新");
    }

    /**
     * 资料变更审核驳回：商家资料保持不变
     */
    @PostMapping("/merchant/update/reject")
    public R<?> rejectMerchantUpdate(@RequestParam Long userId) {
        adminService.rejectMerchantUpdate(userId);
        return R.ok("已驳回该资料变更申请");
    }

    // ==================== 商家商品审核 ====================

    /**
     * 商家商品审核列表
     *
     * @param auditStatus 审核状态过滤：0待审核 1已通过 2已驳回，不传查全部
     */
    @GetMapping("/merchant/product/list")
    public R<PageResult<MerchantProductVO>> getMerchantProductList(MerchantProductQueryDTO query) {
        return R.ok(productService.getAuditProductPage(query));
    }

    /**
     * 商品审核通过（自动上架）
     */
    @PostMapping("/merchant/product/approve")
    public R<?> approveProduct(@RequestParam Long id) {
        productService.auditProduct(id, true);
        return R.ok("已通过审核，商品已上架");
    }

    /**
     * 商品审核驳回（保持下架）
     */
    @PostMapping("/merchant/product/reject")
    public R<?> rejectProduct(@RequestParam Long id) {
        productService.auditProduct(id, false);
        return R.ok("已驳回该商品");
    }

}
