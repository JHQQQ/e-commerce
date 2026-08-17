package com.mitsuha754.ecommerce.service;

import com.mitsuha754.ecommerce.dto.OrderDTO;
import com.mitsuha754.ecommerce.dto.OrderItemDataDTO;
import com.mitsuha754.ecommerce.dto.UserPayDTO;
import com.mitsuha754.ecommerce.entity.Order;
import com.mitsuha754.ecommerce.entity.OrderItem;
import com.mitsuha754.ecommerce.vo.OrderConfirmVO;

import java.util.List;

public interface OrderService {

    /**
     * 预览订单
     * @return 选的商品列表
     */
    List<OrderConfirmVO> getOrdersConfirm(String userName);

    /**
     * 生成订单
     */
    String generateOrder(OrderDTO orderDTO);

    /**
     * 查看订单列表
     */
    List<Order> showOrders(String userName);

    /**
     * 显示倒计时
     */
    Long getExpireTime(String orderNo);

    /**
     * 订单超时取消
     */
    void refreshOrderTimeout(List<Order> orderNoList);

    /**
     * 用户取消订单
     */
    void cancelOrder(String orderNo, String userName);

    /**
     * 添加订单商品
     */
    void addOrderItem(OrderItemDataDTO orderItemDataDTO);

    /**
     * 查看订单商品
     */
    List<OrderItem> getOrderItem(String orderNo, String userName);

    /**
     * 支付
     */
    void toPay(UserPayDTO userPayDTO);

    /**
     * 确认收货
     * @param userPayDTO 用户名+订单
     */
    void receivedOrder(UserPayDTO userPayDTO);
}