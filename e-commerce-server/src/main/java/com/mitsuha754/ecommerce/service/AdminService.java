package com.mitsuha754.ecommerce.service;

import com.mitsuha754.ecommerce.entity.OrderItem;
import com.mitsuha754.ecommerce.vo.AdminOrderListVO;
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
     */
    List<AdminOrderListVO> showAllOrders();

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
}
