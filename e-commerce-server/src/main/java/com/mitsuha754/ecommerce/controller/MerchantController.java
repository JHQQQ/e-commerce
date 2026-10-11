package com.mitsuha754.ecommerce.controller;

import com.mitsuha754.ecommerce.annotation.MerchantOnly;
import com.mitsuha754.ecommerce.dto.ApplyMerchantDTO;
import com.mitsuha754.ecommerce.dto.MerchantProductQueryDTO;
import com.mitsuha754.ecommerce.dto.MerchantUpdateDTO;
import com.mitsuha754.ecommerce.entity.MerchantUpdate;
import com.mitsuha754.ecommerce.entity.OrderItem;
import com.mitsuha754.ecommerce.entity.Product;
import com.mitsuha754.ecommerce.result.PageResult;
import com.mitsuha754.ecommerce.result.R;
import com.mitsuha754.ecommerce.service.MerchantService;
import com.mitsuha754.ecommerce.service.OrderService;
import com.mitsuha754.ecommerce.service.ProductService;
import com.mitsuha754.ecommerce.vo.MerchantOrderVO;
import com.mitsuha754.ecommerce.vo.MerchantStatusVO;
import com.mitsuha754.ecommerce.vo.MerchantVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商家端接口
 * 说明：
 *   - /merchant/status、/merchant/apply 面向所有登录用户（认证入口）
 *   - /merchant/product/* 需要「已通过审核的商家」，双重校验：
 *     ① @MerchantOnly 切面校验 token 角色；② 服务层查商家表确认资格未被撤销
 */
@RestController
@RequestMapping("/merchant")
public class MerchantController {

    @Resource
    private MerchantService merchantService;

    @Resource
    private ProductService productService;

    @Resource
    private OrderService orderService;

    // ==================== 商家认证 ====================

    /**
     * 查询当前登录用户的商家认证状态（前端据此决定入口跳转）
     */
    @GetMapping("/status")
    public R<MerchantStatusVO> getStatus() {
        return R.ok(merchantService.getStatus());
    }

    /**
     * 提交商家认证申请
     */
    @PostMapping("/apply")
    public R<?> applyMerchant(@Valid @RequestBody ApplyMerchantDTO applyMerchantDTO) {
        merchantService.applyMerchant(applyMerchantDTO);
        return R.ok("认证申请已提交，请等待管理员审核");
    }

    /**
     * 查询当前商家的店铺信息
     */
    @GetMapping("/info")
    public R<MerchantVO> getInfo() {
        return R.ok(merchantService.getCurrentMerchant());
    }

    /**
     * 提交店铺资料变更申请
     * 说明：不会直接修改店铺资料，需要管理员在「商家管理 - 资料变更审核」通过后才生效
     */
    @MerchantOnly
    @PostMapping("/info/apply")
    public R<?> applyUpdate(@Valid @RequestBody MerchantUpdateDTO merchantUpdateDTO) {
        merchantService.applyUpdate(merchantUpdateDTO);
        return R.ok("资料变更申请已提交，请等待管理员审核");
    }

    /**
     * 查询自己最近一次资料变更申请（含审核状态与提交内容）
     */
    @MerchantOnly
    @GetMapping("/info/apply")
    public R<MerchantUpdate> getUpdateApply() {
        return R.ok(merchantService.getMyUpdateApply());
    }

    // ==================== 商品管理 ====================

    /**
     * 分页查询自己店铺的商品（含待审核 / 已驳回）
     */
    @MerchantOnly
    @GetMapping("/product/list")
    public R<PageResult<Product>> getProductList(MerchantProductQueryDTO query) {
        Long merchantId = merchantService.requireApprovedMerchantId();
        return R.ok(productService.getMerchantProductPage(query, merchantId));
    }

    /**
     * 新增商品：提交后进入审核状态，管理员审核通过才会在商城上架
     */
    @MerchantOnly
    @PostMapping("/product/add")
    public R<?> addProduct(@RequestBody Product product) {
        Long merchantId = merchantService.requireApprovedMerchantId();
        productService.addMerchantProduct(product, merchantId);
        return R.ok("商品已提交，等待管理员审核");
    }

    /**
     * 修改商品：修改后重新进入审核状态
     */
    @MerchantOnly
    @PostMapping("/product/update")
    public R<?> updateProduct(@RequestBody Product product) {
        Long merchantId = merchantService.requireApprovedMerchantId();
        productService.updateMerchantProduct(product, merchantId);
        return R.ok("商品已修改，需重新审核");
    }

    /**
     * 删除商品（只能删除自己的）
     */
    @MerchantOnly
    @PostMapping("/product/delete")
    public R<?> deleteProduct(@RequestParam Long id) {
        Long merchantId = merchantService.requireApprovedMerchantId();
        productService.deleteMerchantProduct(id, merchantId);
        return R.ok("删除成功");
    }

    /**
     * 上架 / 下架自己的商品（仅审核通过的商品可上架）
     *
     * @param status 1 上架 / 0 下架
     */
    @MerchantOnly
    @PostMapping("/product/status")
    public R<?> updateProductStatus(@RequestParam Long id, @RequestParam Integer status) {
        Long merchantId = merchantService.requireApprovedMerchantId();
        productService.updateMerchantProductStatus(id, status, merchantId);
        return R.ok(status == 1 ? "已上架" : "已下架");
    }

    // ==================== 订单管理 ====================

    /**
     * 订单列表：只返回包含本店商品的订单
     *
     * @param status 订单状态过滤：0待付款 1待发货 2已发货 3已完成 4已取消，不传查全部
     */
    @MerchantOnly
    @GetMapping("/order/list")
    public R<List<MerchantOrderVO>> getOrderList(@RequestParam(required = false) Integer status) {
        Long merchantId = merchantService.requireApprovedMerchantId();
        return R.ok(orderService.getMerchantOrders(merchantId, status));
    }

    /**
     * 订单详情：只返回本店商品明细
     */
    @MerchantOnly
    @GetMapping("/order/detail/{orderNo}")
    public R<List<OrderItem>> getOrderDetail(@PathVariable String orderNo) {
        Long merchantId = merchantService.requireApprovedMerchantId();
        return R.ok(orderService.getMerchantOrderDetail(orderNo, merchantId));
    }

    /**
     * 发货（订单需包含本店商品且处于待发货状态）
     */
    @MerchantOnly
    @PostMapping("/order/deliver")
    public R<?> deliverOrder(@RequestParam String orderNo) {
        Long merchantId = merchantService.requireApprovedMerchantId();
        orderService.merchantDeliver(orderNo, merchantId);
        return R.ok("发货成功");
    }

    /**
     * 取消订单（回退库存与买家余额）
     */
    @MerchantOnly
    @PostMapping("/order/cancel")
    public R<?> cancelOrder(@RequestParam String orderNo) {
        Long merchantId = merchantService.requireApprovedMerchantId();
        orderService.merchantCancel(orderNo, merchantId);
        return R.ok("订单已取消");
    }

}
