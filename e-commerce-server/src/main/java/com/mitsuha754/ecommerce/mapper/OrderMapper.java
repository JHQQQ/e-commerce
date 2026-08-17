package com.mitsuha754.ecommerce.mapper;

import com.mitsuha754.ecommerce.dto.UserPayDTO;
import com.mitsuha754.ecommerce.entity.Order;
import com.mitsuha754.ecommerce.entity.OrderItem;
import com.mitsuha754.ecommerce.vo.OrderConfirmVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {

    /**
     * 预览订单
     */
    List<OrderConfirmVO> getOrderConfirm(@Param("userName")String userName);

    /**
     * 提交订单
     */
    void  generateOrder(@Param("Order") Order order);


    /**
     * 显示已经生成的订单列表
     */
    List<Order> getOrders(@Param("userName")String userName);

    /**
     * 更改订单状态
     */
    void updateOrderStatus(@Param("orderNo")String orderNo, @Param("status")int status);

    /**
     * 校验订单是否存在
     */
    Boolean checkOrderStatus(@Param("orderNo")String orderNo);

    /**
     * 原子操作：扣减库存并校验库存充足
     * @return 影响行数（>0=成功，0=库存不足）
     */
    int deductStockWithCheck(@Param("orderNo") String orderNo);

    /**
     * 校验订单所有明细库存是否充足；返回不足的商品名列表
     */
    List<String> checkAllStock(@Param("orderNo") String orderNo);

    /**
     * 支付后订单取消恢复库存
     */
    void recoverStock(@Param("orderNo")String orderNo);

    /**
     * 支付后取消订单恢复用户余额
     */
    void reconvertBalance(@Param("orderNo")String orderNo);

    /**
     * 查询订单状态
     */
    Integer orderStatus(@Param("orderNo")String orderNo);

    /**
     * 查询所有待支付订单号列表（用于定时关闭超时订单）
     */
    List<String> listPendingPayOrderNos();

    /**
     * 根据订单号查询用户名
     * @param orderNo 订单号
     * @return 用户名
     */
    String selectUserName(@Param("orderNo")String orderNo);

    /**
     * 检查是否达到收货标准
     */
    boolean checkReceiveable(@Param("userPayDTO") UserPayDTO userPayDTO);

    /**
     * 确认收货
     */
    void receiveOrder(@Param("orderNo")String orderNo);
}