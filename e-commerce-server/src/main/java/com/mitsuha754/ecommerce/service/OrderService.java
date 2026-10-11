package com.mitsuha754.ecommerce.service;

import com.mitsuha754.ecommerce.dto.OrderDTO;
import com.mitsuha754.ecommerce.dto.OrderItemDataDTO;
import com.mitsuha754.ecommerce.dto.UserPayDTO;
import com.mitsuha754.ecommerce.entity.Order;
import com.mitsuha754.ecommerce.entity.OrderItem;
import com.mitsuha754.ecommerce.vo.MerchantOrderVO;
import com.mitsuha754.ecommerce.vo.OrderConfirmVO;

import java.util.List;
import java.util.Map;

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
     * 查看订单列表(优化)
     */
    Map<String,List<OrderItem>> getOrderItems(String userName);
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

    // ==================== 商家端订单管理 ====================

    /**
     * 商家端：查询包含本店商品的订单
     *
     * @param merchantId 商家用户ID
     * @param status     订单状态过滤：null 查全部
     */
    List<MerchantOrderVO> getMerchantOrders(Long merchantId, Integer status);

    /**
     * 商家端：订单详情（只返回本店商品明细，需订单包含本店商品）
     */
    List<OrderItem> getMerchantOrderDetail(String orderNo, Long merchantId);

    /**
     * 商家端：发货（订单必须包含本店商品且处于待发货状态）
     */
    void merchantDeliver(String orderNo, Long merchantId);

    /**
     * 商家端：取消订单（回退库存与买家余额）
     */
    void merchantCancel(String orderNo, Long merchantId);
}