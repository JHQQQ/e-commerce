package com.mitsuha754.ecommerce.service;

import com.mitsuha754.ecommerce.entity.OrderItem;
import com.mitsuha754.ecommerce.vo.AdminOrderListVO;
import com.mitsuha754.ecommerce.vo.MerchantUpdateVO;
import com.mitsuha754.ecommerce.vo.MerchantVO;
import com.mitsuha754.ecommerce.vo.UserQueryVO;

import java.math.BigDecimal;
import java.util.List;


public interface AdminService {

    /**
     * 展示用户列表(管理员排除)
     */
    List<UserQueryVO> getAllUsers();

    /**
     * 更改用户状态( 0注销,
     *              1正常,
     *              2禁用)
     */
    void updateUserStatus(Integer id, int status);

    /**
     * 更改用户余额
     */
    void updateUserBalance(Integer id, BigDecimal balance);

    /**
     * 查看订单列表
     *
     * @param status 订单状态过滤：0待付款 1待发货 2已发货 3已完成 4已取消；
     *               null 表示默认只查进行中（待发货/已发货）
     */
    List<AdminOrderListVO> showAllOrders(Integer status);

    /**
     * 发货
     */
    void toDeliver(String orderNo);

    /**
     * 查看订单详情（管理员，不校验归属）
     */
    List<OrderItem> getOrderDetail(String orderNo);

    /**
     * 取消订单（管理员，不校验归属，回退库存与余额）
     */
    void cancelOrder(String orderNo);

    // ==================== 商家管理 ====================

    /**
     * 查询商家列表
     *
     * @param status 商家状态：0审核中 1已通过 2已拒绝 3已撤销；null 表示查全部
     */
    List<MerchantVO> getMerchantList(Integer status);

    /**
     * 商家审核通过：商家状态置为已通过，并把用户角色升级为 MERCHANT
     */
    void approveMerchant(Long userId);

    /**
     * 商家审核拒绝
     */
    void rejectMerchant(Long userId);

    /**
     * 撤销商家权限：商家状态置为已撤销，用户角色降回 USER，该商家商品全部下架
     */
    void revokeMerchant(Long userId);

    // ==================== 商家资料变更审核 ====================

    /**
     * 资料变更申请列表（含商家现有资料做对比）
     *
     * @param status 审核状态：0待审核 1已通过 2已驳回；null 查全部
     */
    List<MerchantUpdateVO> getMerchantUpdateList(Integer status);

    /**
     * 资料变更审核通过：把申请内容写回商家资料
     */
    void approveMerchantUpdate(Long userId);

    /**
     * 资料变更审核驳回：商家资料保持不变
     */
    void rejectMerchantUpdate(Long userId);
}
